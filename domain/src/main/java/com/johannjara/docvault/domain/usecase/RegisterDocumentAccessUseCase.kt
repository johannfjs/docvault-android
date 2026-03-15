package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.repository.DocumentRepository
import javax.inject.Inject

class RegisterDocumentAccessUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(documentId: String) {
        val document = repository.getDocumentById(documentId)
        document?.let {
            val updatedAccessLogs = it.accessLogs + System.currentTimeMillis()
            repository.saveDocument(it.copy(accessLogs = updatedAccessLogs))
        }
    }
}
