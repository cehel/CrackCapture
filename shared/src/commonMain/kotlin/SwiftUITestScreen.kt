import androidx.compose.runtime.Composable

@Composable
fun SwiftUITestScreen() {
    nativeTestView()
}

@Composable
expect fun nativeTestView()
