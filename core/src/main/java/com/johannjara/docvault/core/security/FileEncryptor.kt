package com.johannjara.docvault.core.security

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface FileEncryptor {
    fun getEncryptedOutputStream(file: File): OutputStream
    fun getEncryptedInputStream(file: File): InputStream
}

class FileEncryptorImpl(
    private val context: Context,
    private val keysetHandle: KeysetHandle? = null
) : FileEncryptor {

    init {
        AeadConfig.register()
    }

    private val aead: Aead by lazy {
        val handle = keysetHandle ?: AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
        
        handle.getPrimitive(Aead::class.java)
    }

    override fun getEncryptedOutputStream(file: File): OutputStream {
        return object : ByteArrayOutputStream() {
            override fun close() {
                super.close()
                val ciphertext = aead.encrypt(toByteArray(), file.name.toByteArray())
                file.writeBytes(ciphertext)
            }
        }
    }

    override fun getEncryptedInputStream(file: File): InputStream {
        val ciphertext = file.readBytes()
        val plaintext = aead.decrypt(ciphertext, file.name.toByteArray())
        return plaintext.inputStream()
    }

    companion object {
        private const val KEYSET_NAME = "docvault_keyset"
        private const val PREF_FILE_NAME = "docvault_key_pref"
        private const val MASTER_KEY_URI = "android-keystore://docvault_master_key"
    }
}
