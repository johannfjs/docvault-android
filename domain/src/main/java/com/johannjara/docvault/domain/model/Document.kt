package com.johannjara.docvault.domain.model

data class Document(
    val id: String,
    val name: String,
    val path: String,
    val type: DocumentType,
    val createdAt: Long,
    val locationName: String? = null,
    val accessLogs: List<Long> = emptyList()
)
