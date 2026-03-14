package com.johannjara.docvault.ui.detail

import android.app.Activity
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.johannjara.docvault.R
import com.johannjara.docvault.core.security.ScreenSecurityHelper
import com.johannjara.docvault.core.util.PermissionUtils
import com.johannjara.docvault.design.components.PdfViewer
import com.johannjara.docvault.design.components.WatermarkOverlay
import com.johannjara.docvault.design.components.ZoomableImage
import com.johannjara.docvault.design.components.ZoomableImageModel
import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.model.DocumentUI
import com.johannjara.docvault.design.model.ImmutableByteArray
import com.johannjara.docvault.design.model.ImmutableList
import com.johannjara.docvault.design.theme.DocVaultTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    documentId: String?,
    uiState: DocumentDetailUiState,
    onBackClick: () -> Unit,
    onLoadDocument: (String, FragmentActivity) -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var showLogsSheet by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.all { it }
        if (isGranted) {
            if (documentId != null && activity is FragmentActivity) {
                onLoadDocument(documentId, activity)
            }
        } else {
            onPermissionDenied()
        }
    }

    DisposableEffect(Unit) {
        activity?.let { ScreenSecurityHelper.setScreenSecurity(activity = it, isEnabled = true) }
        onDispose {
            activity?.let {
                ScreenSecurityHelper.setScreenSecurity(
                    activity = it,
                    isEnabled = false
                )
            }
        }
    }

    LaunchedEffect(documentId, uiState.requiresLocationPermission) {
        if (documentId != null && uiState.requiresLocationPermission) {
            permissionLauncher.launch(PermissionUtils.getLocationPermissions())
        } else if (documentId != null && !uiState.requiresLocationPermission &&
            activity is FragmentActivity && !uiState.isAuthenticated && uiState.error == null
        ) {
            onLoadDocument(documentId, activity)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.document?.name ?: stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_content_description)
                        )
                    }
                },
                actions = {
                    if (uiState.isAuthenticated && uiState.document != null) {
                        IconButton(onClick = { showLogsSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(R.string.access_logs_content_description)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: stringResource(R.string.unexpected_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(DocVaultTheme.spacing.medium)
                    )
                }

                uiState.isAuthenticated && uiState.document != null -> {
                    val document = uiState.document!!
                    val watermarkInfo = uiState.watermarkInfo

                    if (watermarkInfo != null) {
                        WatermarkOverlay(info = watermarkInfo) {
                            DocumentContent(
                                document = document,
                                decryptedData = uiState.decryptedData
                            )
                        }
                    } else {
                        DocumentContent(
                            document = document,
                            decryptedData = uiState.decryptedData
                        )
                    }
                }

                uiState.requiresLocationPermission -> {
                    Text(text = stringResource(R.string.requesting_location_permission))
                }

                !uiState.isAuthenticated -> {
                    Text(text = stringResource(R.string.waiting_authentication))
                }
            }
        }

        if (showLogsSheet && uiState.document != null) {
            AccessLogsBottomSheet(
                logs = uiState.document.accessLogs,
                onDismiss = { showLogsSheet = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccessLogsBottomSheet(
    logs: ImmutableList<String>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DocVaultTheme.spacing.medium)
                .padding(bottom = DocVaultTheme.spacing.extraLarge)
        ) {
            Text(
                text = stringResource(R.string.access_logs_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(DocVaultTheme.spacing.medium))
            if (logs.items.isEmpty()) {
                Text(text = stringResource(R.string.no_access_logs))
            } else {
                LazyColumn {
                    items(logs.items) { log ->
                        Text(
                            text = log,
                            modifier = Modifier.padding(vertical = DocVaultTheme.spacing.small),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        HorizontalDivider()
                    }
                }
            }
            Spacer(modifier = Modifier.height(DocVaultTheme.spacing.medium))
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(R.string.close_button))
            }
        }
    }
}

@Composable
private fun DocumentContent(
    document: DocumentUI,
    decryptedData: ImmutableByteArray?
) {
    if (decryptedData == null) {
        CircularProgressIndicator()
        return
    }

    if (document.type == DocumentTypeUI.IMAGE) {
        val bitmap = remember(decryptedData) {
            BitmapFactory.decodeByteArray(decryptedData.data, 0, decryptedData.data.size)
        }
        if (bitmap != null) {
            ZoomableImage(
                model = ZoomableImageModel.BitmapData(bitmap),
                contentDescription = document.name
            )
        } else {
            Text(text = stringResource(R.string.error_processing_image))
        }
    } else if (document.type == DocumentTypeUI.PDF) {
        PdfViewer(pdfBytes = decryptedData)
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    DocVaultTheme {
        DetailScreen(
            documentId = "1",
            uiState = DocumentDetailUiState(
                document = DocumentUI(
                    id = "1",
                    name = stringResource(R.string.test_document_name),
                    type = DocumentTypeUI.PDF,
                    createdAtFormatted = "10/10/2023",
                    path = "",
                    accessLogs = ImmutableList(listOf("10/10/2023 10:00", "11/10/2023 12:00"))
                ),
                isAuthenticated = true
            ),
            onBackClick = {},
            onLoadDocument = { _, _ -> },
            onPermissionDenied = {}
        )
    }
}
