import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import view.photolist.ImageHandler

@Composable
expect fun takePictureNativeView(imageHandler: ImageHandler, redraw: Int = 0)

expect fun getPlatformName(): String

expect fun encodeImageToBase64(image: ImageBitmap): String?

expect fun decodeBase64ToImage(base: String) : ImageBitmap
