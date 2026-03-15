package com.johannjara.docvault.design.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.johannjara.docvault.design.theme.DocVaultTheme

@Immutable
data class WatermarkInfo(
    val location: String,
    val dateTime: String
)

@Composable
fun WatermarkOverlay(
    info: WatermarkInfo,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val watermarkText = "${info.location}\n${info.dateTime}"
    val textStyle = TextStyle(
        color = Color.Gray.copy(alpha = 0.3f),
        fontSize = 20.sp
    )

    Box(modifier = modifier.fillMaxSize()) {
        content()

        Canvas(modifier = Modifier.fillMaxSize()) {
            val textLayoutResult = textMeasurer.measure(
                text = watermarkText,
                style = textStyle
            )

            val canvasWidth = size.width
            val canvasHeight = size.height

            // Aumentamos los pasos para reducir la densidad del watermark
            val stepX = 500f
            val stepY = 500f

            for (x in -100..canvasWidth.toInt() step stepX.toInt()) {
                for (y in -100..canvasHeight.toInt() step stepY.toInt()) {
                    rotate(
                        degrees = -45f,
                        pivot = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat())
                    ) {
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat())
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WatermarkOverlayPreview() {
    DocVaultTheme {
        WatermarkOverlay(
            info = WatermarkInfo(
                location = "Av. Siempre Viva 742",
                dateTime = "20/05/2024 15:30"
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Contenido del Documento",
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                )
            }
        }
    }
}
