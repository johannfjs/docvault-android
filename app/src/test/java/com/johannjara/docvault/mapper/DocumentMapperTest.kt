package com.johannjara.docvault.mapper

import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class DocumentMapperTest {

    @Test
    fun `Document toUI should map correctly`() {
        val timestamp = 1700000000000L
        val document = Document(
            id = "1",
            name = "Test Doc",
            path = "path/to/doc",
            type = DocumentType.PDF,
            createdAt = timestamp
        )

        val uiModel = document.toUI()

        uiModel.id shouldBeEqualTo "1"
        uiModel.name shouldBeEqualTo "Test Doc"
        uiModel.type shouldBeEqualTo DocumentTypeUI.PDF
        uiModel.path shouldBeEqualTo "path/to/doc"
        uiModel.createdAtFormatted.isNotEmpty() shouldBeEqualTo true
    }

    @Test
    fun `DocumentType toUI should map correctly`() {
        DocumentType.PDF.toUI() shouldBeEqualTo DocumentTypeUI.PDF
        DocumentType.IMAGE.toUI() shouldBeEqualTo DocumentTypeUI.IMAGE
    }

    @Test
    fun `DocumentTypeUI toDomain should map correctly`() {
        DocumentTypeUI.PDF.toDomain() shouldBeEqualTo DocumentType.PDF
        DocumentTypeUI.IMAGE.toDomain() shouldBeEqualTo DocumentType.IMAGE
    }
}
