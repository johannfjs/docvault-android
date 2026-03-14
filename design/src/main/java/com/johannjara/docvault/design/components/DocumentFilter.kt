package com.johannjara.docvault.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.johannjara.docvault.design.R
import com.johannjara.docvault.design.model.DocumentTypeUI
import com.johannjara.docvault.design.theme.DocVaultTheme

@Composable
fun DocumentFilter(
    selectedType: DocumentTypeUI?,
    onTypeSelected: (DocumentTypeUI?) -> Unit,
    modifier: Modifier = Modifier
) {
    val types = listOf(null) + DocumentTypeUI.entries

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = DocVaultTheme.spacing.medium,
            vertical = DocVaultTheme.spacing.small
        ),
        horizontalArrangement = Arrangement.spacedBy(DocVaultTheme.spacing.small)
    ) {
        items(types) { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(
                        text = when (type) {
                            null -> stringResource(R.string.filter_document_all)
                            DocumentTypeUI.PDF -> stringResource(R.string.filter_document_pdfs)
                            DocumentTypeUI.IMAGE -> stringResource(R.string.filter_document_images)
                        }
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DocumentFilterAllSelectedPreview() {
    DocVaultTheme {
        DocumentFilter(
            selectedType = null,
            onTypeSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DocumentFilterPdfSelectedPreview() {
    DocVaultTheme {
        DocumentFilter(
            selectedType = DocumentTypeUI.PDF,
            onTypeSelected = {}
        )
    }
}
