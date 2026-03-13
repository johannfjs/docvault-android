package com.johannjara.docvault.core.storage

import android.content.Context
import android.net.Uri
import com.johannjara.docvault.core.security.FileEncryptor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

interface FileStorageHelper {
    suspend fun saveAndEncryptFile(uri: Uri, fileName: String): File?
    fun getFile(fileName: String): File
    fun deleteFile(fileName: String): Boolean
}

class FileStorageHelperImpl @Inject constructor(
    private val context: Context,
    private val fileEncryptor: FileEncryptor,
    private val dispatcher: CoroutineDispatcher
) : FileStorageHelper {

    private val secureFolder: File
        get() = File(context.filesDir, SECURE_FOLDER_NAME).apply {
            if (!exists()) mkdirs()
        }

    override suspend fun saveAndEncryptFile(uri: Uri, fileName: String): File? = withContext(dispatcher) {
        val targetFile = File(secureFolder, fileName)
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                fileEncryptor.getEncryptedOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            targetFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getFile(fileName: String): File {
        return File(secureFolder, fileName)
    }

    override fun deleteFile(fileName: String): Boolean {
        val file = getFile(fileName)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    companion object {
        private const val SECURE_FOLDER_NAME = "secure_vault"
    }
}
