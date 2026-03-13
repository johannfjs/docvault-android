package com.johannjara.docvault.domain.model

data class Document(
    val id: String,
    val name: String,
    val path: String,
    val type: DocumentType,
    val createdAt: Long
)
