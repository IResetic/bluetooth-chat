package dev.skybit.bluetoothchat.availableconnections.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.skybit.bluetoothchat.availableconnections.data.controller.BluetoothController
import dev.skybit.bluetoothchat.availableconnections.data.controller.BluetoothControllerImpl

@Module
@InstallIn(SingletonComponent::class)
interface AvailableConnectionsModule {

    @Binds
    fun provideBluetoothController(impl: BluetoothControllerImpl): BluetoothController
}
