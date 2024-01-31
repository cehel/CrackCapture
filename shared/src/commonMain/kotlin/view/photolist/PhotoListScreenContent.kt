package view.photolist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import model.PhotoInfo
import takePictureNativeView

@Composable
fun PhotoListScreen() {

    val viewModel = getViewModel(Unit, viewModelFactory {
        PhotoListScreenViewModel(
            PhotoItemRepository()
        )
    })

    val showCamera by viewModel.showCameraView.collectAsState()

    var buttonClick: Int by rememberSaveable {
        mutableStateOf<Int>(0)
    }

    val imageHandler = remember {
        object : ImageHandler {
            override fun onImageBitmapCaptured(bitmap: ImageBitmap) =
                viewModel.onImageBitmapCaptured(bitmap)

            override fun onCancelled() = viewModel.onCaptureCancelled()
        }
    }

    PhotoListScreenContent(
        showCamera = showCamera,
        deletePhoto = viewModel::deletePhotoItem,
        photos = viewModel.photoInfoList,
        onOpenCameraButtonClicked = {
            viewModel.showCameraView()
            buttonClick++
        },
        buttonClick = buttonClick,
        imageHandler = imageHandler
    )

}

@Composable
fun PhotoListScreenContent(
    showCamera: Boolean,
    photos: SnapshotStateList<PhotoInfo>,
    deletePhoto: (PhotoInfo) -> Unit = {},
    onOpenCameraButtonClicked: () -> Unit,
    buttonClick: Int,
    imageHandler: ImageHandler
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        PhotoCardList(photos, deletePhoto)
        Button(onClick = {
            onOpenCameraButtonClicked.invoke()
        }) {
            Text("Capture photo")
        }
        if (showCamera) {
            CameraScreen(
                buttonClick,
                imageHandler
            )
        }
    }
}

@Composable
private fun PhotoCardList(
    photos: SnapshotStateList<PhotoInfo>,
    deletePhoto: (PhotoInfo) -> Unit = {}
) {
    LazyRow {
        items(photos.size) {
            PhotoCard(photos[it], deletePhoto)
        }
    }
}

@Composable
fun CameraScreen(
    buttonClick: Int,
    imageHandler: ImageHandler
) {
    Column(modifier = Modifier.height(50.dp).fillMaxWidth()) {
        // Your overlay content goes here$

        takePictureNativeView(imageHandler, buttonClick)
    }
}

