package com.johannjara.docvault.design.model

import androidx.compose.runtime.Immutable

@Immutable
enum class DocumentTypeUI {
    PDF,
    IMAGE
}

@Immutable
data class DocumentUI(
    val id: String,
    val name: String,
    val type: DocumentTypeUI,
    val createdAtFormatted: String,
    val path: String,
    val accessLogs: ImmutableList<String> = ImmutableList(emptyList())
)
