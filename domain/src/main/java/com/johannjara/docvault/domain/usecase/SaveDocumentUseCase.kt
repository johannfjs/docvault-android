package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.repository.DocumentRepository

class SaveDocumentUseCase(private val repository: DocumentRepository) {
    suspend operator fun invoke(document: Document) {
        repository.saveDocument(document = document)
    }
}
