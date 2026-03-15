package com.johannjara.docvault.ui.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.model.DocumentUI
import com.johannjara.docvault.design.model.ImmutableList
import com.johannjara.docvault.design.theme.DocVaultTheme
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingState_showsCircularProgressIndicator() {
        composeTestRule.setContent {
            DocVaultTheme {
                DetailScreen(
                    documentId = "1",
                    uiState = DocumentDetailUiState(isLoading = true),
                    onBackClick = {},
                    onLoadDocument = { _, _ -> },
                    onPermissionDenied = {}
                )
            }
        }
    }

    @Test
    fun errorState_showsErrorMessage() {
        val errorMessage = "Error de carga"
        composeTestRule.setContent {
            DocVaultTheme {
                DetailScreen(
                    documentId = "1",
                    uiState = DocumentDetailUiState(error = errorMessage),
                    onBackClick = {},
                    onLoadDocument = { _, _ -> },
                    onPermissionDenied = {}
                )
            }
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun authenticatedState_showsDocumentName() {
        val documentName = "Documento Secreto"
        val uiState = DocumentDetailUiState(
            document = DocumentUI(
                id = "1",
                name = documentName,
                type = DocumentTypeUI.PDF,
                createdAtFormatted = "10/10/2023",
                path = "",
                accessLogs = ImmutableList(emptyList())
            ),
            isAuthenticated = true
        )

        composeTestRule.setContent {
            DocVaultTheme {
                DetailScreen(
                    documentId = "1",
                    uiState = uiState,
                    onBackClick = {},
                    onLoadDocument = { _, _ -> },
                    onPermissionDenied = {}
                )
            }
        }

        composeTestRule.onNodeWithText(documentName).assertIsDisplayed()
    }
}
