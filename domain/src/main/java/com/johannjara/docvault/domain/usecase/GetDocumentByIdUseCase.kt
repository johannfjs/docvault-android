package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.repository.DocumentRepository
import javax.inject.Inject

class GetDocumentByIdUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(id: String): Document? {
        return repository.getDocumentById(id = id)
    }
}
