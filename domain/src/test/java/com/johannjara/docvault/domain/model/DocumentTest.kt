package com.johannjara.docvault.domain.model

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class DocumentTest {

    @Test
    fun `document should store values correctly`() {
        val id = "1"
        val name = "test.pdf"
        val path = "/path/to/test.pdf"
        val type = DocumentType.PDF
        val createdAt = 123456789L

        val document = Document(
            id = id,
            name = name,
            path = path,
            type = type,
            createdAt = createdAt
        )

        document.id shouldBeEqualTo id
        document.name shouldBeEqualTo name
        document.path shouldBeEqualTo path
        document.type shouldBeEqualTo type
        document.createdAt shouldBeEqualTo createdAt
    }
}
