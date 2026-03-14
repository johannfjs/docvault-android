package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import com.johannjara.docvault.domain.repository.DocumentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RegisterDocumentAccessUseCaseTest {

    private val repository: DocumentRepository = mockk(relaxed = true)
    private val registerDocumentAccessUseCase =
        RegisterDocumentAccessUseCase(repository = repository)

    @Test
    fun `invoke should add current time to access logs and save document`() = runTest {
        val documentId = "1"
        val existingDocument = Document(
            id = documentId,
            name = "Test",
            path = "path",
            type = DocumentType.PDF,
            createdAt = 1000L,
            accessLogs = listOf(500L)
        )
        coEvery { repository.getDocumentById(documentId) } returns existingDocument

        registerDocumentAccessUseCase(documentId)

        coVerify {
            repository.saveDocument(withArg {
                it.id shouldBeEqualTo documentId
                it.accessLogs.size shouldBeEqualTo 2
                it.accessLogs[0] shouldBeEqualTo 500L
            })
        }
    }

    private infix fun <T> T.shouldBeEqualTo(expected: T) {
        assert(this == expected) { "Expected $expected but was $this" }
    }
}
