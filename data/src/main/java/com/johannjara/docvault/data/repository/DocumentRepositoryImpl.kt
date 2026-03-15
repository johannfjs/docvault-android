package com.johannjara.docvault.data.repository

import androidx.core.net.toUri
import com.johannjara.docvault.core.security.FileEncryptor
import com.johannjara.docvault.core.storage.FileStorageHelper
import com.johannjara.docvault.data.local.dao.DocumentDao
import com.johannjara.docvault.data.local.entity.DocumentEntity
import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import com.johannjara.docvault.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
    private val fileStorageHelper: FileStorageHelper,
    private val fileEncryptor: FileEncryptor
) : DocumentRepository {

    override fun getDocuments(type: DocumentType?): Flow<List<Document>> {
        val flow = if (type == null) {
            documentDao.getAllDocuments()
        } else {
            documentDao.getDocumentsByType(type = type.name)
        }
        return flow.map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveDocument(document: Document) {
        val existingDocument = documentDao.getDocumentById(document.id)

        if (existingDocument != null && existingDocument.path == document.path) {
            documentDao.insertDocument(DocumentEntity.fromDomain(document))
            return
        }

        val sourceUri = if (document.path.startsWith("/")) {
            File(document.path).toUri()
        } else {
            document.path.toUri()
        }

        val secureFile = fileStorageHelper.saveAndEncryptFile(
            uri = sourceUri,
            fileName = document.name
        )

        secureFile?.let { file ->
            val encryptedDocument = document.copy(path = file.absolutePath)
            documentDao.insertDocument(DocumentEntity.fromDomain(encryptedDocument))
        } ?: throw Exception("Failed to encrypt and save file")
    }

    override suspend fun getDocumentById(id: String): Document? {
        return documentDao.getDocumentById(id)?.toDomain()
    }

    override suspend fun getDocumentContent(path: String): Result<ByteArray> {
        return try {
            val file = File(path)
            if (!file.exists()) return Result.failure(Exception("File not found"))

            val inputStream = fileEncryptor.getEncryptedInputStream(file)
            val bytes = inputStream.readBytes()
            inputStream.close()
            Result.success(bytes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
