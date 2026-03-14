package com.johannjara.docvault.ui.detail

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johannjara.docvault.core.location.LocationProvider
import com.johannjara.docvault.core.security.BiometricAuthenticator
import com.johannjara.docvault.core.util.PermissionUtils
import com.johannjara.docvault.design.components.WatermarkInfo
import com.johannjara.docvault.design.model.ImmutableByteArray
import com.johannjara.docvault.domain.usecase.DecryptDocumentUseCase
import com.johannjara.docvault.domain.usecase.GetDocumentByIdUseCase
import com.johannjara.docvault.domain.usecase.RegisterDocumentAccessUseCase
import com.johannjara.docvault.mapper.toUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DocumentDetailViewModel @Inject constructor(
    private val getDocumentByIdUseCase: GetDocumentByIdUseCase,
    private val registerDocumentAccessUseCase: RegisterDocumentAccessUseCase,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val decryptDocumentUseCase: DecryptDocumentUseCase,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentDetailUiState())
    val uiState: StateFlow<DocumentDetailUiState> = _uiState.asStateFlow()

    fun loadDocument(documentId: String, activity: FragmentActivity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val document = getDocumentByIdUseCase(documentId)
            if (document != null) {
                if (!PermissionUtils.hasLocationPermission(activity)) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            requiresLocationPermission = true,
                            document = document.toUI()
                        )
                    }
                    return@launch
                }

                val currentLocation =
                    locationProvider.getCurrentLocationName() ?: "Ubicación desconocida"
                _uiState.update {
                    it.copy(
                        document = document.toUI(),
                        watermarkInfo = WatermarkInfo(
                            location = currentLocation,
                            dateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                .format(Date())
                        ),
                        requiresLocationPermission = false
                    )
                }
                authenticate(activity, documentId, document.path)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Documento no encontrado") }
            }
        }
    }

    fun onLocationPermissionDenied() {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = "Se requiere el permiso de ubicación para visualizar el documento",
                requiresLocationPermission = false
            )
        }
    }

    private fun authenticate(activity: FragmentActivity, documentId: String, path: String) {
        if (biometricAuthenticator.isBiometricAvailable()) {
            biometricAuthenticator.authenticate(
                activity = activity,
                title = "Autenticación Requerida",
                subtitle = "Por favor, autentícate para visualizar el documento",
                onSuccess = {
                    viewModelScope.launch {
                        decryptAndShow(documentId, path)
                    }
                },
                onError = { _, errString ->
                    _uiState.update { it.copy(isLoading = false, error = "Error: $errString") }
                },
                onFailed = {
                    _uiState.update { it.copy(isLoading = false, error = "Autenticación fallida") }
                }
            )
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Seguridad biométrica no disponible"
                )
            }
        }
    }

    private suspend fun decryptAndShow(documentId: String, path: String) {
        decryptDocumentUseCase(path).fold(
            onSuccess = { bytes ->
                registerDocumentAccessUseCase(documentId)
                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        isLoading = false,
                        decryptedData = ImmutableByteArray(bytes)
                    )
                }
            },
            onFailure = { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al desencriptar: ${error.message}"
                    )
                }
            }
        )
    }
}

