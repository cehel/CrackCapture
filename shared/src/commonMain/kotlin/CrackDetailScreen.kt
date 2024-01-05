import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import org.jetbrains.skia.Image

@Composable
fun CrackDetailScreen() {

    val viewModel = getViewModel(Unit, viewModelFactory { CrackDetailViewModel() })

    val showOverlay by viewModel.showCameraView.collectAsState()

    if (showOverlay) {
        OverlayScreen(viewModel)
    } else {
        InitialScreen(onOpenOverlay = { viewModel.showCameraView() }, viewModel)
    }
}

@Composable
fun InitialScreen(onOpenOverlay: () -> Unit, viewModel: CrackDetailViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = onOpenOverlay) {
            Text("Open Overlay")
        }
        MyImageDisplay(viewModel)
    }
}

@Composable
fun OverlayScreen(viewModel: CrackDetailViewModel) {
    Column(modifier = Modifier.height(50.dp).fillMaxWidth()) {
        // Your overlay content goes here$
        takePictureNativeView(viewModel)
    }
}

@Composable
fun MyImageDisplay(viewModel: CrackDetailViewModel) {
    val imageBytes by viewModel.imageBytes.collectAsState()
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Here is the image")
        imageBytes?.let {
            Text("Image Received: ${it.size}")
            val image = Image.makeFromEncoded(it)
            Image(
                bitmap = image.toComposeImageBitmap(),
                contentDescription = null
            )
        }
    }
}

@Composable
expect fun takePictureNativeView(imageHandler: ImageHandler)
