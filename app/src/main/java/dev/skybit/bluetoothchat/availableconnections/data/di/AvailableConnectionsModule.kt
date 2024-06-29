package dev.skybit.bluetoothchat.availableconnections.data.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.skybit.bluetoothchat.availableconnections.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.availableconnections.data.controller.BluetoothControllerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AvailableConnectionsModule {

    @Binds
    @Singleton
    fun provideBluetoothController(impl: BluetoothControllerImpl): BluetoothController

    companion object {
        @Provides
        @Singleton
        fun provideContext(@ApplicationContext context: Context): Context {
            return context
        }
    }
}
