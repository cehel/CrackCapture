package view.photolist

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import cafe.adriel.voyager.core.screen.Screen
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import domain.realmConfigWithName
import io.realm.kotlin.Realm
import model.PhotoInfo
import takePictureNativeView
import view.navigation.ScreenKeys
import view.ui.LargeDropdownMenu


class PhotoListScreen(val crackId: Long, val crackLogId: String) : Screen {

    override val key = ScreenKeys.CRACKITEM.name

    @Composable
    override fun Content() {
        val viewModel = getViewModel(Unit, viewModelFactory {
            val realmConfig = realmConfigWithName("CrackLogDB")
            val realm = Realm.open(realmConfig)
            PhotoListScreenViewModel(
                crackLogId = crackLogId,
                crackId = crackId,
                PhotoItemRepository(
                    realm
                )
            )
        })

        val uiState by viewModel.editCrackUIState.collectAsState()

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
            uiState = uiState,
            deletePhoto = viewModel::deletePhotoItem,
            photos = viewModel.photoInfoList,
            onOpenCameraButtonClicked = {
                viewModel.showCameraView()
                buttonClick++
            },
            saveOrientation = viewModel::saveOrientation,
            saveWidth = viewModel::saveWidth,
            saveLength = viewModel::saveLength,
            buttonClick = buttonClick,
            imageHandler = imageHandler
        )
    }
}

@Composable
fun PhotoListScreenContent(
    uiState: EditCrackUIState,
    photos: SnapshotStateList<PhotoInfo>,
    deletePhoto: (PhotoInfo) -> Unit = {},
    onOpenCameraButtonClicked: () -> Unit,
    saveOrientation: (String) -> Unit,
    saveWidth: (String) -> Unit,
    saveLength: (String) -> Unit,
    buttonClick: Int,
    imageHandler: ImageHandler
) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PhotoCardList(photos, deletePhoto)
        Button(onClick = {
            onOpenCameraButtonClicked.invoke()
        }) {
            Text("Capture photo")
        }
        if (uiState.showCamera) {
            CameraScreen(
                buttonClick,
                imageHandler
            )
        }

        // Displaying the timestamp


        // Orientation Picker

        var selectedIndex by remember { mutableStateOf(-1) }
        selectedIndex = uiState.orientationChoices.indexOf(uiState.orientation)
        LargeDropdownMenu(
            label = "Orientation",
            items = Orientation.values().map { it.name },
            selectedIndex = selectedIndex,
            onItemSelected = { index, _ ->
                saveOrientation(uiState.orientationChoices[index])
            }
        )

        // Width Picker, similar to Orientation Picker
        var selectedLengthIndex by remember { mutableStateOf(-1) }
        selectedLengthIndex = uiState.lengthChoices.indexOf(uiState.length)
        LargeDropdownMenu(
            label = "Length",
            items = uiState.lengthChoices,
            selectedIndex = selectedLengthIndex,
            onItemSelected = { index, _ ->
                saveLength(uiState.lengthChoices[index])
            }
        )

        // Width Picker, similar to Orientation Picker
        var selectedWidthIndex by remember { mutableStateOf(-1) }
        selectedWidthIndex = uiState.widthChoices.indexOf(uiState.width)
        LargeDropdownMenu(
            label = "Width",
            items = uiState.widthChoices,
            selectedIndex = selectedWidthIndex,
            onItemSelected = { index, _ ->
                saveWidth(uiState.widthChoices[index])
            }
        )

        // Length Picker, similar to Orientation Picker

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

