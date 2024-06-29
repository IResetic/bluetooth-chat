package dev.skybit.bluetoothchat.core.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.skybit.bluetoothchat.core.presentation.utils.BuildVersionProvider
import dev.skybit.bluetoothchat.core.presentation.utils.BuildVersionProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CorePresentationModule {

    @Binds
    @Singleton
    fun provideBuildVersionProvider(impl: BuildVersionProviderImpl): BuildVersionProvider
}
