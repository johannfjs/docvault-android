package com.johannjara.docvault.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val path: String,
    val type: String,
    val createdAt: Long,
    val locationName: String?,
    val accessLogs: List<Long>
) {
    fun toDomain() = Document(
        id = id,
        name = name,
        path = path,
        type = DocumentType.valueOf(type),
        createdAt = createdAt,
        locationName = locationName,
        accessLogs = accessLogs
    )

    companion object {
        fun fromDomain(document: Document) = DocumentEntity(
            id = document.id,
            name = document.name,
            path = document.path,
            type = document.type.name,
            createdAt = document.createdAt,
            locationName = document.locationName,
            accessLogs = document.accessLogs
        )
    }
}
