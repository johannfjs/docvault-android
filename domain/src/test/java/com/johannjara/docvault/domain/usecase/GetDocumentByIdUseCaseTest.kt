package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import com.johannjara.docvault.domain.repository.DocumentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.Test

class GetDocumentByIdUseCaseTest {

    private val repository: DocumentRepository = mockk()
    private val getDocumentByIdUseCase = GetDocumentByIdUseCase(repository)

    @Test
    fun `invoke should return document from repository when id exists`() = runTest {
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
        coVerify(exactly = 1) { repository.getDocumentById(id = documentId) }
    }

    @Test
    fun `invoke should return null when repository returns null`() = runTest {
        val documentId = "non-existent-id"
        coEvery { repository.getDocumentById(id = documentId) } returns null

        val result = getDocumentByIdUseCase(id = documentId)

        result shouldBeEqualTo null
        coVerify(exactly = 1) { repository.getDocumentById(id = documentId) }
    }

    @Test
    fun `invoke should propagate exception when repository fails`() = runTest {
        val documentId = "error-id"
        val errorMessage = "Database error"
        coEvery { repository.getDocumentById(id = any()) } throws RuntimeException(errorMessage)

        val action = suspend { getDocumentByIdUseCase(id = documentId) }

        action shouldThrow RuntimeException::class
    }
}