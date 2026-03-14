package com.johannjara.docvault.mapper

import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.model.DocumentUI
import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Document.toUI(): DocumentUI {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return DocumentUI(
        id = id,
        name = name,
        type = type.toUI(),
        createdAtFormatted = dateFormat.format(Date(createdAt)),
        path = path,
        accessLogs = accessLogs.map { dateFormat.format(Date(it)) }
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
