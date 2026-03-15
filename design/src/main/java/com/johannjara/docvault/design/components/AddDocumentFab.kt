package com.johannjara.docvault.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.johannjara.docvault.design.R
import com.johannjara.docvault.design.theme.DocVaultTheme

@Composable
fun AddDocumentFab(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDocumentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = { showMenu = !showMenu },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_document_content_description)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.add_document_camera)) },
                onClick = {
                    showMenu = false
                    onCameraClick()
                },
                leadingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.add_document_gallery)) },
                onClick = {
                    showMenu = false
                    onGalleryClick()
                },
                leadingIcon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.add_document_pdf)) },
                onClick = {
                    showMenu = false
                    onDocumentClick()
                },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) }
            )
        }
    }
}

@Preview
@Composable
private fun AddDocumentFabPreview() {
    DocVaultTheme {
        AddDocumentFab(
            onCameraClick = {},
            onGalleryClick = {},
            onDocumentClick = {}
        )
    }
}
