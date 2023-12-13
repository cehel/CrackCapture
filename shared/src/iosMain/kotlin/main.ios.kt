import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIView
import platform.UIKit.UIViewController


actual fun getPlatformName(): String = "iOS"

lateinit var uiFactory: () -> UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun nativeTestView() {
    UIKitView(
        factory = uiFactory,
        modifier = Modifier.size(300.dp)
            .border(2.dp, androidx.compose.ui.graphics.Color.Blue),
    )
}


@OptIn(ExperimentalForeignApi::class)
fun MainViewController(createUIView: () -> UIView): UIViewController =
    ComposeUIViewController {
        uiFactory = createUIView
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SwiftUITestScreen()
            //CrackCaptureEntryScreen()
            Text("How to use SwiftUI inside Compose Multiplatform")
        }
    }