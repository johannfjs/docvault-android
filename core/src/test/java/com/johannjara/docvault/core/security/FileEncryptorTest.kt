package com.johannjara.docvault.core.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FileEncryptorTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var fileEncryptor: FileEncryptor

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        fileEncryptor = FileEncryptorImpl(context)
    }

    @Test
    fun `should encrypt and decrypt data correctly`() {
        val file = temporaryFolder.newFile("test.txt")
        val originalText = "Hello DocVault Secure World"
        file.writeText(originalText)

        val outputStream = fileEncryptor.getEncryptedOutputStream(file)
        outputStream.use { it.write(originalText.toByteArray()) }

        val inputStream = fileEncryptor.getEncryptedInputStream(file)
        val decryptedText = inputStream.bufferedReader().use { it.readText() }

        decryptedText shouldBeEqualTo originalText
    }
}
