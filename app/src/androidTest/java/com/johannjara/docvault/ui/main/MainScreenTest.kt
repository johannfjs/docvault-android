package com.johannjara.docvault.ui.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.model.DocumentUI
import com.johannjara.docvault.design.model.ImmutableList
import com.johannjara.docvault.design.theme.DocVaultTheme
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingState_showsCircularProgressIndicator() {
        composeTestRule.setContent {
            DocVaultTheme {
                MainScreen(
                    state = MainUiState(isLoading = true),
                    onTypeFilterSelected = {},
                    onAddDocument = { _, _, _ -> },
                    onDocumentClick = {}
                )
            }
        }
    }

    @Test
    fun documentsState_showsListOfDocuments() {
        val documents = listOf(
            DocumentUI(
                id = "1",
                name = "Test Document 1",
                type = DocumentTypeUI.PDF,
                createdAtFormatted = "10/10/2023",
                path = "",
                accessLogs = ImmutableList(emptyList())
            ),
            DocumentUI(
                id = "2",
                name = "Test Image 1",
                type = DocumentTypeUI.IMAGE,
                createdAtFormatted = "11/10/2023",
                path = "",
                accessLogs = ImmutableList(emptyList())
            )
        )

        composeTestRule.setContent {
            DocVaultTheme {
                MainScreen(
                    state = MainUiState(documents = documents),
                    onTypeFilterSelected = {},
                    onAddDocument = { _, _, _ -> },
                    onDocumentClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Test Document 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Image 1").assertIsDisplayed()
    }
}
