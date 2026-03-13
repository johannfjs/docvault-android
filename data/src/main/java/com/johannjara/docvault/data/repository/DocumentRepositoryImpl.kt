package com.johannjara.docvault.data.repository

import androidx.core.net.toUri
import com.johannjara.docvault.core.storage.FileStorageHelper
import com.johannjara.docvault.data.local.dao.DocumentDao
import com.johannjara.docvault.data.local.entity.DocumentEntity
import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import com.johannjara.docvault.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
    private val fileStorageHelper: FileStorageHelper
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
        val sourceUri = document.path.toUri()
        val secureFile = fileStorageHelper.saveAndEncryptFile(
            uri = sourceUri,
            fileName = document.name
        )

        secureFile?.let { file ->
            val encryptedDocument = document.copy(path = file.absolutePath)
            documentDao.insertDocument(DocumentEntity.fromDomain(encryptedDocument))
        } ?: throw Exception("Failed to encrypt and save file")
    }
}
