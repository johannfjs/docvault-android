package com.johannjara.docvault.ui.detail

import com.johannjara.docvault.design.components.WatermarkInfo
import com.johannjara.docvault.design.model.DocumentUI
import com.johannjara.docvault.design.model.ImmutableByteArray

data class DocumentDetailUiState(
    val isLoading: Boolean = false,
    val document: DocumentUI? = null,
    val watermarkInfo: WatermarkInfo? = null,
    val isAuthenticated: Boolean = false,
    val decryptedData: ImmutableByteArray? = null,
    val error: String? = null,
    val requiresLocationPermission: Boolean = false
)