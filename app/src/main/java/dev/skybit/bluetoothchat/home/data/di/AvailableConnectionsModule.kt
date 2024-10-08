package dev.skybit.bluetoothchat.home.data.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.skybit.bluetoothchat.home.data.controller.BluetoothControllerImpl
import dev.skybit.bluetoothchat.home.data.repository.ChatRepositoryImpl
import dev.skybit.bluetoothchat.home.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.home.domain.repository.ChatRepository
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

    @Binds
    fun provideChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
