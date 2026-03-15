package com.johannjara.docvault.domain.usecase

import com.johannjara.docvault.domain.repository.DocumentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class DecryptDocumentUseCaseTest {

    private val repository: DocumentRepository = mockk()
    private val decryptDocumentUseCase = DecryptDocumentUseCase(repository)

    @Test
    fun `invoke should return success result from repository`() = runTest {
        val path = "path/to/doc"
        val expectedData = byteArrayOf(1, 2, 3)
        coEvery { repository.getDocumentContent(path) } returns Result.success(expectedData)

        val result = decryptDocumentUseCase(path)

        result.isSuccess shouldBeEqualTo true
        result.getOrNull() shouldBeEqualTo expectedData
        coVerify { repository.getDocumentContent(path) }
    }

    @Test
    fun `invoke should return failure result from repository`() = runTest {
        val path = "path/to/doc"
        val expectedException = Exception("Decryption failed")
        coEvery { repository.getDocumentContent(path) } returns Result.failure(expectedException)

        val result = decryptDocumentUseCase(path)

        result.isFailure shouldBeEqualTo true
        result.exceptionOrNull() shouldBeEqualTo expectedException
        coVerify { repository.getDocumentContent(path) }
    }
}
