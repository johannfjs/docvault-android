package com.johannjara.docvault.design.model

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableList<T>(
    val items: List<T>
)
