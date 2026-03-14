package com.johannjara.docvault.design.components

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.johannjara.docvault.design.theme.DocVaultTheme

@Immutable
sealed interface ZoomableImageModel {
    data class Url(val url: String) : ZoomableImageModel
    data class LocalUri(val uri: Uri) : ZoomableImageModel
    data class Resource(@DrawableRes val resId: Int) : ZoomableImageModel
}

@Composable
fun ZoomableImage(
    model: ZoomableImageModel,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val imageSource = when (model) {
        is ZoomableImageModel.Url -> model.url
        is ZoomableImageModel.LocalUri -> model.uri
        is ZoomableImageModel.Resource -> model.resId
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    
                    val extraWidth = (scale - 1) * size.width
                    val extraHeight = (scale - 1) * size.height
                    val maxX = extraWidth / 2
                    val maxY = extraHeight / 2

                    offsetX = (offsetX + pan.x * scale).coerceIn(-maxX, maxX)
                    offsetY = (offsetY + pan.y * scale).coerceIn(-maxY, maxY)
                }
            }
    ) {
        AsyncImage(
            model = imageSource,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ZoomableImageUrlPreview() {
    DocVaultTheme {
        ZoomableImage(
            model = ZoomableImageModel.Url("https://example.com/image.jpg"),
            contentDescription = "Preview Image"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ZoomableImageResourcePreview() {
    DocVaultTheme {
        ZoomableImage(
            model = ZoomableImageModel.Resource(android.R.drawable.ic_menu_gallery),
            contentDescription = "Preview Resource"
        )
    }
}
