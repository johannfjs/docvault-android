package com.johannjara.docvault.ui.main

import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.domain.model.Document
import com.johannjara.docvault.domain.model.DocumentType
import com.johannjara.docvault.domain.usecase.GetDocumentsUseCase
import com.johannjara.docvault.domain.usecase.SaveDocumentUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val getDocumentsUseCase: GetDocumentsUseCase = mockk()
    private val saveDocumentUseCase: SaveDocumentUseCase = mockk()
    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getDocumentsUseCase(any()) } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDocuments should update state with documents`() = runTest {
        val domainDocuments = listOf(
            Document("1", "Doc 1", "path/1", DocumentType.PDF, 123456789L),
            Document("2", "Img 1", "path/2", DocumentType.IMAGE, 123456790L)
        )
        coEvery { getDocumentsUseCase(null) } returns flowOf(domainDocuments)

        viewModel = MainViewModel(getDocumentsUseCase, saveDocumentUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.value.documents.size shouldBeEqualTo 2
        viewModel.state.value.documents[0].name shouldBeEqualTo "Doc 1"
        viewModel.state.value.documents[0].type shouldBeEqualTo DocumentTypeUI.PDF
        viewModel.state.value.isLoading.shouldBeFalse()
    }

    @Test
    fun `onTypeFilterSelected should update selectedType and reload documents`() = runTest {
        coEvery { getDocumentsUseCase(DocumentType.IMAGE) } returns flowOf(emptyList())

        viewModel = MainViewModel(getDocumentsUseCase, saveDocumentUseCase)
        viewModel.onTypeFilterSelected(DocumentTypeUI.IMAGE)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.value.selectedType shouldBeEqualTo DocumentTypeUI.IMAGE
        coVerify { getDocumentsUseCase(DocumentType.IMAGE) }
    }
}
