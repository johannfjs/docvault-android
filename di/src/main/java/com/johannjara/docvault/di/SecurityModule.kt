package com.johannjara.docvault.di

import android.content.Context
import com.johannjara.docvault.core.security.BiometricAuthenticator
import com.johannjara.docvault.core.security.BiometricAuthenticatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideBiometricAuthenticator(@ApplicationContext context: Context): BiometricAuthenticator {
        return BiometricAuthenticatorImpl(context)
    }
}
