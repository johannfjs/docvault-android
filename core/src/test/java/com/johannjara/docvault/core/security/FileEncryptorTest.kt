package com.johannjara.docvault.core.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.security.GeneralSecurityException

@RunWith(RobolectricTestRunner::class)
class FileEncryptorTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var fileEncryptor: FileEncryptor
    private lateinit var keysetHandle: KeysetHandle

    @Before
    fun setup() {
        AeadConfig.register()
        context = ApplicationProvider.getApplicationContext()
        keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"))
        fileEncryptor = FileEncryptorImpl(context, keysetHandle)
    }

    @Test
    fun `should encrypt and decrypt data correctly`() {
        val file = temporaryFolder.newFile("test.txt")
        val originalText = "Hello DocVault Secure World"

        val outputStream = fileEncryptor.getEncryptedOutputStream(file)
        outputStream.use { it.write(originalText.toByteArray()) }

        val inputStream = fileEncryptor.getEncryptedInputStream(file)
        val decryptedText = inputStream.bufferedReader().use { it.readText() }

        decryptedText shouldBeEqualTo originalText
    }

    @Test
    fun `should fail to decrypt if file name (associated data) changes`() {
        val originalFile = temporaryFolder.newFile("original.txt")
        val originalText = "Sensitive Data"

        val outputStream = fileEncryptor.getEncryptedOutputStream(originalFile)
        outputStream.use { it.write(originalText.toByteArray()) }

        val renamedFile = File(originalFile.parent, "renamed.txt")
        originalFile.renameTo(renamedFile)

        val decryptAction = {
            fileEncryptor.getEncryptedInputStream(renamedFile)
        }

        decryptAction shouldThrow GeneralSecurityException::class
    }

    @Test
    fun `should fail to decrypt if data is corrupted`() {
        val file = temporaryFolder.newFile("corrupted.txt")
        val originalText = "Data to be corrupted"

        val outputStream = fileEncryptor.getEncryptedOutputStream(file)
        outputStream.use { it.write(originalText.toByteArray()) }

        val content = file.readBytes()
        content[0] = content[0].inc()
        file.writeBytes(content)

        val decryptAction = {
            fileEncryptor.getEncryptedInputStream(file)
        }

        decryptAction shouldThrow GeneralSecurityException::class
    }

    @Test
    fun `should encrypt and decrypt empty data correctly`() {
        val file = temporaryFolder.newFile("empty.txt")
        val originalText = ""

        val outputStream = fileEncryptor.getEncryptedOutputStream(file)
        outputStream.use { it.write(originalText.toByteArray()) }

        val inputStream = fileEncryptor.getEncryptedInputStream(file)
        val decryptedText = inputStream.bufferedReader().use { it.readText() }

        decryptedText shouldBeEqualTo originalText
    }

    @Test
    fun `should use default keyset handle when not provided`() {
        val defaultEncryptor = FileEncryptorImpl(context)
        val file = temporaryFolder.newFile("default_keyset.txt")
        val text = "Test with default keyset"

        val outputStream = defaultEncryptor.getEncryptedOutputStream(file)
        outputStream.use { it.write(text.toByteArray()) }

        val inputStream = defaultEncryptor.getEncryptedInputStream(file)
        val decryptedText = inputStream.bufferedReader().use { it.readText() }

        decryptedText shouldBeEqualTo text
    }
}
