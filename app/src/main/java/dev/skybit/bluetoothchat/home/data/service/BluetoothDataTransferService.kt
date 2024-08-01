package dev.skybit.bluetoothchat.home.data.service

import android.bluetooth.BluetoothSocket
import com.squareup.moshi.Moshi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.skybit.bluetoothchat.core.data.di.IoDispatcher
import dev.skybit.bluetoothchat.home.data.model.BluetoothMessageDto
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothDataTransferService @AssistedInject constructor(
    @Assisted private val bluetoothSocket: BluetoothSocket,
    private val moshi: Moshi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val adapter = moshi.adapter(BluetoothMessageDto::class.java)

    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        return flow {
            if (!bluetoothSocket.isConnected) return@flow

            val buffer = ByteArray(1024)

            while (true) {
                val byteCount = try {
                    bluetoothSocket.inputStream.read(buffer)
                } catch (e: IOException) {
                    // TODO Add error handeling
                    throw IOException("Error")
                }

                val message = adapter.fromJson(buffer.decodeToString(endIndex = byteCount))

                message?.toDomain(false)?.let { emit(it) }
            }
        }.flowOn(ioDispatcher)
    }

    suspend fun sendMessage(message: BluetoothMessage): Boolean {
        return withContext(ioDispatcher) {
            try {
                val jsonMessage = adapter.toJson(BluetoothMessageDto.fromDomain(message))
                bluetoothSocket.outputStream.write(jsonMessage.toByteArray())
            } catch (e: IOException) {
                return@withContext false
            }

            true
        }
    }
}
