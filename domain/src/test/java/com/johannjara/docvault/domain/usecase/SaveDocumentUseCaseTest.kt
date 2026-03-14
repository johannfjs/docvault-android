package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import com.johannjara.docvault.domain.repository.DocumentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SaveDocumentUseCaseTest {

    private val repository = mockk<DocumentRepository>()
    private val useCase = SaveDocumentUseCase(repository = repository)

    @Test
    fun `invoke should call repository saveDocument`() = runBlocking {
        val document = Document(
            id = "1",
            name = "test.pdf",
            path = "/path",
            type = DocumentType.PDF,
            createdAt = 123L
        )
        coEvery { repository.saveDocument(document = document) } returns Unit

        useCase(document = document)

        coVerify { repository.saveDocument(document) }
    }
}
