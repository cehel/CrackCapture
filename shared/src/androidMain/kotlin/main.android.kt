import androidx.compose.material.Text
import androidx.compose.runtime.Composable

actual fun getPlatformName(): String = "Android"

@Composable
actual fun nativeTestView(imageHandler: ImageHandler) {
    Text("Android Native View")
}

@Composable
fun MainView() = BerufswahlScreen()
