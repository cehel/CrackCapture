package model

import androidx.compose.ui.graphics.ImageBitmap

data class CrackReport(
    val description: String,
    val place: String,
    val photos: List<ImageBitmap> // ImageBitmap for the photos
)
