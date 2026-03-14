package com.johannjara.docvault.ui.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.johannjara.docvault.R
import com.johannjara.docvault.core.util.PermissionUtils
import com.johannjara.docvault.design.components.AddDocumentFab
import com.johannjara.docvault.design.components.DocumentFilter
import com.johannjara.docvault.design.components.DocumentItem
import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.model.DocumentUI
import com.johannjara.docvault.design.model.ImmutableList
import com.johannjara.docvault.design.theme.DocVaultTheme
import com.johannjara.docvault.design.theme.LocalSpacing
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainState,
    onTypeFilterSelected: (DocumentTypeUI?) -> Unit,
    onAddDocument: (Uri, String, DocumentTypeUI) -> Unit,
    onDocumentClick: (String) -> Unit
) {
    val context = LocalContext.current
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAddDocument(it, "Img_${System.currentTimeMillis()}", DocumentTypeUI.IMAGE) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                onAddDocument(uri, "Cam_${System.currentTimeMillis()}", DocumentTypeUI.IMAGE)
            }
        }
    }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAddDocument(it, "File_${System.currentTimeMillis()}", DocumentTypeUI.PDF) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            pendingAction?.invoke()
        }
        pendingAction = null
    }

    fun createTempPictureUri(): Uri {
        val tempFile =
            File.createTempFile("picture_${System.currentTimeMillis()}", ".jpg", context.cacheDir)
                .apply {
                    createNewFile()
                    deleteOnExit()
                }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.app_name)) })
        },
        floatingActionButton = {
            AddDocumentFab(
                onCameraClick = {
                    val action = {
                        val uri = createTempPictureUri()
                        tempPhotoUri = uri
                        cameraLauncher.launch(uri)
                    }
                    if (PermissionUtils.hasCameraPermission(context)) {
                        action()
                    } else {
                        pendingAction = action
                        permissionLauncher.launch(PermissionUtils.getCameraPermissions())
                    }
                },
                onGalleryClick = {
                    val action = { galleryLauncher.launch("image/*") }
                    val permissions = PermissionUtils.getStoragePermissions()
                    if (PermissionUtils.hasPermissions(context, permissions)) {
                        action()
                    } else {
                        pendingAction = action
                        permissionLauncher.launch(permissions)
                    }
                },
                onDocumentClick = {
                    val action = { documentLauncher.launch("application/pdf") }
                    val permissions = PermissionUtils.getStoragePermissions()
                    if (PermissionUtils.hasPermissions(context, permissions)) {
                        action()
                    } else {
                        pendingAction = action
                        permissionLauncher.launch(permissions)
                    }
                }
            )
        }
    ) { paddingValues ->
        MainContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onTypeFilterSelected = onTypeFilterSelected,
            onDocumentClick = onDocumentClick
        )
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    state: MainState,
    onTypeFilterSelected: (DocumentTypeUI?) -> Unit,
    onDocumentClick: (String) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        DocumentFilter(
            selectedType = state.selectedType,
            onTypeSelected = onTypeFilterSelected
        )

        if (state.isLoading) {
            LoadingIndicator()
        } else {
            DocumentList(
                documents = state.documents,
                onDocumentClick = onDocumentClick
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DocumentList(
    documents: List<DocumentUI>,
    onDocumentClick: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing.medium)
    ) {
        items(
            items = documents,
            key = { it.id }
        ) { document ->
            DocumentItem(
                document = document,
                onClick = { onDocumentClick(document.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    DocVaultTheme {
        MainScreen(
            state = MainState(
                documents = listOf(
                    DocumentUI(
                        id = "1",
                        name = "Documento 1",
                        type = DocumentTypeUI.PDF,
                        createdAtFormatted = "10/10/2023",
                        path = "",
                        accessLogs = ImmutableList(emptyList())
                    ),
                    DocumentUI(
                        id = "2",
                        name = "Imagen 1",
                        type = DocumentTypeUI.IMAGE,
                        createdAtFormatted = "11/10/2023",
                        path = "",
                        accessLogs = ImmutableList(emptyList())
                    )
                )
            ),
            onTypeFilterSelected = {},
            onAddDocument = { _, _, _ -> },
            onDocumentClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenLoadingPreview() {
    DocVaultTheme {
        MainScreen(
            state = MainState(isLoading = true),
            onTypeFilterSelected = {},
            onAddDocument = { _, _, _ -> },
            onDocumentClick = {}
        )
    }
}
