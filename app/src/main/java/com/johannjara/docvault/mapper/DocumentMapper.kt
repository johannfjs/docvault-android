package com.johannjara.docvault.mapper

import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.model.DocumentUI
import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Document.toUI(): DocumentUI {
    return DocumentUI(
        id = id,
        name = name,
        type = type.toUI(),
        createdAtFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(createdAt)),
        path = path
    )
}

fun DocumentType.toUI(): DocumentTypeUI {
    return when (this) {
        DocumentType.PDF -> DocumentTypeUI.PDF
        DocumentType.IMAGE -> DocumentTypeUI.IMAGE
    }
}

fun DocumentTypeUI.toDomain(): DocumentType {
    return when (this) {
        DocumentTypeUI.PDF -> DocumentType.PDF
        DocumentTypeUI.IMAGE -> DocumentType.IMAGE
    }
}
