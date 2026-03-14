package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import com.johannjara.docvault.domain.repository.DocumentRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class GetDocumentByIdUseCaseTest {

    private val repository: DocumentRepository = mockk()
    private val getDocumentByIdUseCase = GetDocumentByIdUseCase(repository)

    @Test
    fun `invoke should return document from repository`() = runTest {
        val documentId = "1"
        val expectedDocument = Document(
            id = documentId,
            name = "Test Document",
            path = "path/to/document",
            type = DocumentType.PDF,
            createdAt = 123456789L
        )
        coEvery { repository.getDocumentById(id = documentId) } returns expectedDocument

        val result = getDocumentByIdUseCase(id = documentId)

        result shouldBeEqualTo expectedDocument
    }

    @Test
    fun `invoke should return null when repository returns null`() = runTest {
        val documentId = "1"
        coEvery { repository.getDocumentById(documentId) } returns null

        val result = getDocumentByIdUseCase(documentId)

        result shouldBeEqualTo null
    }
}
