package com.johannjara.docvault.ui.main

import androidx.compose.runtime.Immutable
import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.model.DocumentUI

@Immutable
data class MainUiState(
    val documents: List<DocumentUI> = emptyList(),
    val selectedType: DocumentTypeUI? = null,
    val isLoading: Boolean = false
)
