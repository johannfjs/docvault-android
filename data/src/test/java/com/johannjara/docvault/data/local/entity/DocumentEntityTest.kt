package com.johannjara.docvault.data.local.entity

import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class DocumentEntityTest {

    @Test
    fun `toDomain should map correctly`() {
        val entity = DocumentEntity(
            id = "1",
            name = "test.pdf",
            path = "/path",
            type = "PDF",
            createdAt = 123L,
            accessLogs = listOf(100L, 200L)
        )

        val domain = entity.toDomain()

        domain.id shouldBeEqualTo entity.id
        domain.name shouldBeEqualTo entity.name
        domain.path shouldBeEqualTo entity.path
        domain.type shouldBeEqualTo DocumentType.PDF
        domain.createdAt shouldBeEqualTo entity.createdAt
        domain.accessLogs shouldBeEqualTo entity.accessLogs
    }

    @Test
    fun `fromDomain should map correctly`() {
        val domain = Document(
            id = "1",
            name = "test.pdf",
            path = "/path",
            type = DocumentType.IMAGE,
            createdAt = 123L,
            accessLogs = listOf(100L, 200L)
        )

        val entity = DocumentEntity.fromDomain(domain)

        entity.id shouldBeEqualTo domain.id
        entity.name shouldBeEqualTo domain.name
        entity.path shouldBeEqualTo domain.path
        entity.type shouldBeEqualTo "IMAGE"
        entity.createdAt shouldBeEqualTo domain.createdAt
        entity.accessLogs shouldBeEqualTo domain.accessLogs
    }
}
