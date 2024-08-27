package dev.skybit.bluetoothchat.home.data.model

import com.squareup.moshi.JsonClass
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage

@JsonClass(generateAdapter = true)
data class BluetoothMessageDto(
    val id: String,
    val message: String,
    val senderName: String,
    val sendTimeAndDate: String,
    val isFromLocalUser: Boolean
) {
    fun toDomain(isLocal: Boolean) = BluetoothMessage(
        id = id,
        message = message,
        senderName = senderName,
        sendTimeAndDate = sendTimeAndDate,
        isFromLocalUser = isLocal
    )

    companion object {
        fun fromDomain(bluetoothMessage: BluetoothMessage) = BluetoothMessageDto(
            id = bluetoothMessage.id,
            message = bluetoothMessage.message,
            senderName = bluetoothMessage.senderName,
            sendTimeAndDate = bluetoothMessage.sendTimeAndDate,
            isFromLocalUser = bluetoothMessage.isFromLocalUser
        )
    }
}
