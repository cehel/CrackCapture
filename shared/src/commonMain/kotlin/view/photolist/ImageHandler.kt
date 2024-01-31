package view.photolist

import androidx.compose.ui.graphics.ImageBitmap

interface ImageHandler {

    fun onImageBitmapCaptured(bitmap: ImageBitmap)
    fun onCancelled()
}
