package dev.skybit.bluetoothchat.home.data.controller

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import dev.skybit.bluetoothchat.core.data.di.IoDispatcher
import dev.skybit.bluetoothchat.core.presentation.utils.BuildVersionProvider
import dev.skybit.bluetoothchat.home.data.mappers.toBluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.data.recevers.BluetoothStateReceiver
import dev.skybit.bluetoothchat.home.data.recevers.FoundDeviceReceiver
import dev.skybit.bluetoothchat.home.data.service.BluetoothDataTransferService
import dev.skybit.bluetoothchat.home.data.service.BluetoothDataTransferServiceFactory
import dev.skybit.bluetoothchat.home.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.domain.model.BluetoothError
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.home.domain.model.ChatInfo
import dev.skybit.bluetoothchat.home.domain.model.ConnectionResult
import dev.skybit.bluetoothchat.home.domain.model.ConnectionResult.TransferSucceeded
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BluetoothControllerImpl @Inject constructor(
    private val context: Context,
    private val bluetoothDataTransferServiceFactory: BluetoothDataTransferServiceFactory,
    buildVersionProvider: BuildVersionProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BluetoothController {

    private val bluetoothManager by lazy { context.getSystemService(BluetoothManager::class.java) }
    private val bluetoothAdapter by lazy { bluetoothManager?.adapter }
    private var dataTransferService: BluetoothDataTransferService? = null

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceInfo>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceInfo>>
        get() = _pairedDevices.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver(buildVersionProvider) { device ->
        updateScannedDevices(device)
    }

    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }
        } else {
            CoroutineScope(ioDispatcher).launch {
                _errors.emit("Can't connect to a non-paired device.")
            }
        }
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    init {
        updatePairedDevices()
        registerBluetoothStateReceiver()
    }

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        registerFoundDeviceReceiver()

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        bluetoothAdapter?.cancelDiscovery()
    }

    override fun release() {
        try {
            context.unregisterReceiver(foundDeviceReceiver)
            context.unregisterReceiver(bluetoothStateReceiver)
        } catch (e: IllegalArgumentException) {
            Log.d("BluetoothController", "Catch unregister recever")
        }

        closeConnection()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }
            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "bluetooth_chat_service",
                UUID.fromString(SERVICE_UUID)
            )

            var shouldLoop = true
            while (shouldLoop) {
                currentClientSocket = try {
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    // TODO Check what should we do here
                    null
                }

                currentClientSocket?.let { socket ->
                    currentServerSocket?.close()
                    emit(
                        ConnectionResult.ConnectionEstablished(
                            chatInfo = ChatInfo(
                                chatId = socket.remoteDevice.address,
                                senderName = socket.remoteDevice.name ?: "Unknown name",
                                lastMessage = ""
                            )
                        )
                    )

                    handelMessage(this, socket)
                }
            }
        }.onCompletion { _ ->
            closeConnection()
        }.flowOn(ioDispatcher)
    }

    override fun connectToDevice(device: BluetoothDeviceInfo): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            currentClientSocket = bluetoothAdapter
                ?.getRemoteDevice(device.address)
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID)
                )
            stopDiscovery()

            currentClientSocket?.let { socket ->
                try {
                    socket.connect()
                    emit(
                        ConnectionResult.ConnectionEstablished(
                            chatInfo = ChatInfo(
                                chatId = socket.remoteDevice.address,
                                senderName = socket.remoteDevice.name ?: "Unknown name",
                                lastMessage = ""
                            )
                        )
                    )

                    handelMessage(this, socket)
                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    // emit(ConnectionResult.Error("Connection was interrupted $e"))
                    emit(ConnectionResult.Error(BluetoothError.CONNECTION_INTERRUPTED))
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(ioDispatcher)
    }

    @SuppressLint("HardwareIds")
    override suspend fun trySendMessage(message: String): ConnectionResult {
        // if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) { return ConnectionResult.Error("You don't have bluetooth permission") }
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) { return ConnectionResult.Error(BluetoothError.NO_BLUETOOTH_CONNECT_PERMISSION) }

        // if (dataTransferService == null) { return ConnectionResult.Error("No connection established") }
        if (dataTransferService == null) { return ConnectionResult.Error(BluetoothError.NO_CONNECTION_ESTABLISHED) }

        val bluetoothMessage = createBluetoothMessage(message)

        val isMessageSend = dataTransferService?.sendMessage(bluetoothMessage) ?: false

        // return if (isMessageSend) TransferSucceeded(bluetoothMessage) else ConnectionResult.Error("Message was not sent")
        return if (isMessageSend) TransferSucceeded(bluetoothMessage) else ConnectionResult.Error(BluetoothError.MESSAGE_NOT_SENT)
    }

    override fun closeServerConnection() {
        currentServerSocket?.close()
        currentServerSocket = null
    }

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    private fun createBluetoothMessage(message: String): BluetoothMessage {
        return BluetoothMessage(
            id = UUID.randomUUID().toString(),
            chatId = currentClientSocket?.remoteDevice?.address ?: "Unknown address",
            message = message,
            senderName = bluetoothAdapter?.name ?: "Unknown name",
            sendTimeAndDate = System.currentTimeMillis().toString(),
            isFromLocalUser = true
        )
    }

    private fun registerFoundDeviceReceiver() {
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
    }

    private fun registerBluetoothStateReceiver() {
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )
    }

    private fun updateScannedDevices(device: BluetoothDevice) {
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceInfo()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    private fun updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) { return }

        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceInfo() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun handelMessage(controller: FlowCollector<ConnectionResult>, socket: BluetoothSocket) {
        bluetoothDataTransferServiceFactory.create(socket).also { service ->
            dataTransferService = service

            controller.emitAll(
                service
                    .listenForIncomingMessages()
                    .map {
                        TransferSucceeded(it)
                    }
            )
        }
    }

    companion object {
        // This value needs to be hardcoded because both devices need to use the same UUID
        const val SERVICE_UUID = "882b3006-5486-4ac7-bfc8-b01fb4a5a790"
    }
}
