package com.johannjara.docvault.design.components

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.createBitmap
import com.johannjara.docvault.design.model.ImmutableByteArray
import com.johannjara.docvault.design.theme.DocVaultTheme
import java.io.File
import java.io.FileOutputStream
import kotlin.use

@Composable
fun PdfViewer(pdfBytes: ImmutableByteArray) {
    val context = LocalContext.current
    val renderer = remember(pdfBytes) {
        try {
            val tempFile = File.createTempFile("temp_pdf", ".pdf", context.cacheDir)
            FileOutputStream(tempFile).use { it.write(pdfBytes.data) }
            val input = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            PdfRenderer(input).also {
                tempFile.deleteOnExit()
            }
        } catch (e: Exception) {
            null
        }
    }

    if (renderer == null) {
        return
    }

    val pageCount = renderer.pageCount

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(count = pageCount) { pageIndex ->
            val bitmap = remember(pageIndex) {
                renderer.openPage(pageIndex).use { page ->
                    val bitmap = createBitmap(page.width, page.height)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmap
                }
            }
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Page ${pageIndex + 1}",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }

    DisposableEffect(renderer) {
        onDispose {
            renderer.close()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PdfViewerPreview() {
    DocVaultTheme {
        PdfViewer(pdfBytes = ImmutableByteArray(ByteArray(0)))
    }
}
