package com.johannjara.docvault.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.model.DocumentUI
import com.johannjara.docvault.design.theme.DocVaultTheme

@Composable
fun DocumentItem(
    document: DocumentUI,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = DocVaultTheme.spacing.extraSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(DocVaultTheme.spacing.medium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (document.type == DocumentTypeUI.IMAGE) {
                    Icons.Default.Image
                } else {
                    Icons.Default.Description
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(DocVaultTheme.spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = document.createdAtFormatted,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DocumentItemPdfPreview() {
    DocVaultTheme {
        DocumentItem(
            document = DocumentUI(
                id = "1",
                name = "Mi Documento.pdf",
                type = DocumentTypeUI.PDF,
                createdAtFormatted = "12/03/2026 10:30",
                path = ""
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DocumentItemImagePreview() {
    DocVaultTheme {
        DocumentItem(
            document = DocumentUI(
                id = "2",
                name = "Foto_DNI.jpg",
                type = DocumentTypeUI.IMAGE,
                createdAtFormatted = "13/03/2026 15:45",
                path = "https://example.com/image.jpg"
            )
        )
    }
}
