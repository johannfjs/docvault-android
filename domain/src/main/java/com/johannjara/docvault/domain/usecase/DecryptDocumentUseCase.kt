package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.repository.DocumentRepository
import javax.inject.Inject

class DecryptDocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(path: String): Result<ByteArray> {
        return repository.getDocumentContent(path = path)
    }
}
