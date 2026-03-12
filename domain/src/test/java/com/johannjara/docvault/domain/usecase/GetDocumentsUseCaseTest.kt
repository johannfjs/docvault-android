package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import com.johannjara.docvault.domain.repository.DocumentRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class GetDocumentsUseCaseTest {

    private val repository = mockk<DocumentRepository>()
    private val useCase = GetDocumentsUseCase(repository)

    @Test
    fun `invoke should call repository getDocuments`(): Unit = runBlocking {
        val type = DocumentType.PDF
        val documents = listOf(
            Document(
                id = "1",
                name = "test.pdf",
                path = "/path",
                type = DocumentType.PDF,
                createdAt = 123L
            )
        )
        every { repository.getDocuments(type) } returns flowOf(documents)

        val result = useCase(type).first()

        verify { repository.getDocuments(type) }
        result shouldBeEqualTo documents
    }
}
