package com.johannjara.docvault.core.di

import android.content.Context
import com.johannjara.docvault.core.security.FileEncryptor
import com.johannjara.docvault.core.security.FileEncryptorImpl
import com.johannjara.docvault.core.storage.FileStorageHelper
import com.johannjara.docvault.core.storage.FileStorageHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideFileEncryptor(@ApplicationContext context: Context): FileEncryptor {
        return FileEncryptorImpl(context)
    }

    @Provides
    @Singleton
    fun provideFileStorageHelper(
        @ApplicationContext context: Context,
        fileEncryptor: FileEncryptor,
        dispatcher: CoroutineDispatcher
    ): FileStorageHelper {
        return FileStorageHelperImpl(context, fileEncryptor, dispatcher)
    }
}
