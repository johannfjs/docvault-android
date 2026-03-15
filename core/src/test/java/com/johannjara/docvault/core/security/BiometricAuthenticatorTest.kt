package com.johannjara.docvault.core.security

import android.content.Context
import androidx.biometric.BiometricManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Test

class BiometricAuthenticatorTest {

    private val context: Context = mockk()
    private val biometricManager: BiometricManager = mockk()
    private lateinit var authenticator: BiometricAuthenticatorImpl

    @Before
    fun setup() {
        mockkStatic(BiometricManager::class)
        every { BiometricManager.from(context) } returns biometricManager
        authenticator = BiometricAuthenticatorImpl(context)
    }

    @After
    fun tearDown() {
        unmockkStatic(BiometricManager::class)
    }

    @Test
    fun `isBiometricAvailable should return true when BIOMETRIC_SUCCESS`() {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        every { biometricManager.canAuthenticate(authenticators) } returns BiometricManager.BIOMETRIC_SUCCESS

        val result = authenticator.isBiometricAvailable()

        result shouldBeEqualTo true
    }

    @Test
    fun `isBiometricAvailable should return false when not BIOMETRIC_SUCCESS`() {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        every { biometricManager.canAuthenticate(authenticators) } returns BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE

        val result = authenticator.isBiometricAvailable()

        result shouldBeEqualTo false
    }
}
