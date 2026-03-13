package com.johannjara.docvault.ui.main

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.model.DocumentUI
import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.usecase.GetDocumentsUseCase
import com.johannjara.docvault.domain.usecase.SaveDocumentUseCase
import com.johannjara.docvault.mapper.toDomain
import com.johannjara.docvault.mapper.toUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

@Immutable
data class MainState(
    val documents: List<DocumentUI> = emptyList(),
    val selectedType: DocumentTypeUI? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val saveDocumentUseCase: SaveDocumentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        loadDocuments()
    }

    private fun loadDocuments() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getDocumentsUseCase(_state.value.selectedType?.toDomain())
                .map { docs -> docs.map { it.toUI() } }
                .collect { docs ->
                    _state.update { it.copy(documents = docs, isLoading = false) }
                }
        }
    }

    fun onTypeFilterSelected(type: DocumentTypeUI?) {
        _state.update { it.copy(selectedType = type) }
        loadDocuments()
    }

    fun onAddDocument(context: Context, uri: Uri, name: String, type: DocumentTypeUI) {
        viewModelScope.launch {
            val internalUri = copyFileToInternalStorage(context, uri, name, type)
            val document = Document(
                id = UUID.randomUUID().toString(),
                name = name,
                path = internalUri.toString(),
                type = type.toDomain(),
                createdAt = System.currentTimeMillis()
            )
            saveDocumentUseCase(document)

            val file = File(internalUri.path ?: "")
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private fun copyFileToInternalStorage(
        context: Context,
        uri: Uri,
        name: String,
        type: DocumentTypeUI
    ): Uri {
        val extension = if (type == DocumentTypeUI.PDF) "pdf" else "jpg"
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "$name.$extension")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return Uri.fromFile(file)
    }
}
