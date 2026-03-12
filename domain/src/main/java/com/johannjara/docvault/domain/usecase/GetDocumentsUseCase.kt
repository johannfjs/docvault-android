package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import com.johannjara.docvault.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow

class GetDocumentsUseCase(private val repository: DocumentRepository) {
    operator fun invoke(type: DocumentType? = null): Flow<List<Document>> {
        return repository.getDocuments(type = type)
    }
}
