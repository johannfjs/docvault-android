package com.johannjara.docvault.ui.detail

import androidx.fragment.app.FragmentActivity
import com.johannjara.docvault.core.location.LocationProvider
import com.johannjara.docvault.core.security.BiometricAuthenticator
import com.johannjara.docvault.core.util.PermissionUtils
import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.model.DocumentUI
import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import com.johannjara.docvault.domain.usecase.DecryptDocumentUseCase
import com.johannjara.docvault.domain.usecase.GetDocumentByIdUseCase
import com.johannjara.docvault.domain.usecase.RegisterDocumentAccessUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DocumentDetailViewModelTest {

    private val getDocumentByIdUseCase: GetDocumentByIdUseCase = mockk()
    private val registerDocumentAccessUseCase: RegisterDocumentAccessUseCase = mockk(relaxed = true)
    private val biometricAuthenticator: BiometricAuthenticator = mockk()
    private val decryptDocumentUseCase: DecryptDocumentUseCase = mockk()
    private val locationProvider: LocationProvider = mockk()
    private val activity: FragmentActivity = mockk()

    private lateinit var viewModel: DocumentDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(PermissionUtils)
        viewModel = DocumentDetailViewModel(
            getDocumentByIdUseCase,
            registerDocumentAccessUseCase,
            biometricAuthenticator,
            decryptDocumentUseCase,
            locationProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(PermissionUtils)
    }

    @Test
    fun `loadDocument should set error when document not found`() = runTest {
        // Given
        val documentId = "1"
        coEvery { getDocumentByIdUseCase(documentId) } returns null

        // When
        viewModel.loadDocument(documentId, activity)
        advanceUntilIdle()

        // Then
        viewModel.uiState.value.error shouldBeEqualTo "Documento no encontrado"
        viewModel.uiState.value.isLoading shouldBeEqualTo false
    }

    @Test
    fun `loadDocument should request location permission if not granted`() = runTest {
        // Given
        val documentId = "1"
        val document = Document(documentId, "Doc", "path", DocumentType.PDF, 0L)
        coEvery { getDocumentByIdUseCase(documentId) } returns document
        every { PermissionUtils.hasLocationPermission(activity) } returns false

        // When
        viewModel.loadDocument(documentId, activity)
        advanceUntilIdle()

        // Then
        viewModel.uiState.value.requiresLocationPermission shouldBeEqualTo true
        viewModel.uiState.value.document?.id shouldBeEqualTo documentId
    }

    @Test
    fun `onLocationPermissionDenied should update state with error`() = runTest {
        // When
        viewModel.onLocationPermissionDenied()

        // Then
        viewModel.uiState.value.error shouldBeEqualTo "Se requiere el permiso de ubicación para visualizar el documento"
        viewModel.uiState.value.requiresLocationPermission shouldBeEqualTo false
    }
}
