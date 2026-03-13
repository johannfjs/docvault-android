package com.johannjara.docvault.data.repository

import com.johannjara.docvault.core.storage.FileStorageHelper
import com.johannjara.docvault.data.local.dao.DocumentDao
import com.johannjara.docvault.data.local.entity.DocumentEntity
import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class DocumentRepositoryImplTest {

    private val documentDao: DocumentDao = mockk()
    private val fileStorageHelper: FileStorageHelper = mockk()
    private lateinit var repository: DocumentRepositoryImpl

    @Before
    fun setup() {
        repository = DocumentRepositoryImpl(documentDao, fileStorageHelper)
    }

    @Test
    fun `getDocuments should return list of documents from dao`() = runTest {
        val entities = listOf(
            DocumentEntity(
                id = "1",
                name = "Doc 1",
                path = "/path/1",
                type = "PDF",
                createdAt = 123L
            ),
            DocumentEntity(
                id = "2",
                name = "Doc 2",
                path = "/path/2",
                type = "IMAGE",
                createdAt = 456L
            )
        )
        every { documentDao.getAllDocuments() } returns flowOf(entities)

        val result = repository.getDocuments(null).first()

        result.size shouldBeEqualTo 2
        result[0].id shouldBeEqualTo "1"
        result[0].type shouldBeEqualTo DocumentType.PDF
        result[1].id shouldBeEqualTo "2"
        result[1].type shouldBeEqualTo DocumentType.IMAGE
    }

    @Test
    fun `saveDocument should encrypt file and then save to dao`() = runTest {
        val originalPath = "content://media/external/images/media/1"
        val fileName = "my_id.jpg"
        val securePath = "/secure/path/my_id.jpg"
        val document = Document(
            id = "1",
            name = fileName,
            path = originalPath,
            type = DocumentType.IMAGE,
            createdAt = 123L
        )

        val secureFile = mockk<File>()
        every { secureFile.absolutePath } returns securePath

        coEvery { fileStorageHelper.saveAndEncryptFile(any(), fileName) } returns secureFile
        coEvery { documentDao.insertDocument(any()) } returns Unit

        repository.saveDocument(document)

        coVerify { fileStorageHelper.saveAndEncryptFile(any(), fileName) }
        coVerify {
            documentDao.insertDocument(withArg {
                it.path shouldBeEqualTo securePath
                it.name shouldBeEqualTo fileName
                it.type shouldBeEqualTo DocumentType.IMAGE.name
            })
        }
    }

    @Test(expected = Exception::class)
    fun `saveDocument should throw exception if file encryption fails`() = runTest {
        val document = Document(
            id = "1",
            name = "name",
            path = "path",
            type = DocumentType.PDF,
            createdAt = 123L
        )
        coEvery { fileStorageHelper.saveAndEncryptFile(any(), any()) } returns null

        repository.saveDocument(document)
    }
}
