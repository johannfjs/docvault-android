package com.johannjara.docvault.domain.repository

import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getDocuments(type: DocumentType? = null): Flow<List<Document>>
    suspend fun saveDocument(document: Document)
    suspend fun getDocumentById(id: String): Document?
}
