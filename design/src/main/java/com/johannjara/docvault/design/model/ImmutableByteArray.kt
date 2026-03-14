package com.johannjara.docvault.design.model

import androidx.compose.runtime.Immutable

@Immutable
class ImmutableByteArray(
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImmutableByteArray) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int = data.contentHashCode()
}