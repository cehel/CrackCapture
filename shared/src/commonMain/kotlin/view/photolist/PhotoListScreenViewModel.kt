package view.photolist

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import data.CrackItem
import data.PhotoItem
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import encodeImageToBase64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.PhotoInfo
import org.mongodb.kbson.ObjectId

class PhotoListScreenViewModel(
    val crackLogId: String,
    val crackId: Long,
    val photoRepo: PhotoItemRepository
) : ViewModel() {


    private val _editCrackUIState = MutableStateFlow(EditCrackUIState())
    val editCrackUIState: StateFlow<EditCrackUIState> = _editCrackUIState

    val photoInfoList: SnapshotStateList<PhotoInfo> = mutableStateListOf()

    init {
        viewModelScope.launch {
            photoRepo.crackItemForId(crackLogId = crackLogId, crackItemId = crackId).collect {
                it?.let {
                    println("orientation: ${it.orientation}")
                    println("title: ${it.title}")
                    println("length: ${it.length}")
                    updateUIStateWith(it)
                }
            }
        }
        viewModelScope.launch {
            listenForPhotos(crackLogId = crackLogId, crackItemId = crackId)
        }
    }

    private fun updateUIStateWith(crack: CrackItem) {
        _editCrackUIState.update {
            it.copy(
                orientation = crack.orientation,
                title = crack.title,
                width = crack.width,
                length = crack.length
            )
        }
    }

    private suspend fun listenForPhotos(crackLogId: String, crackItemId: Long) {

        photoRepo.photoItemsForCrackLogAndItemId(
            crackLogId = crackLogId,
            crackItemId = crackItemId
        )
            .collect { photolist ->
                photoInfoList.clear()
                photoInfoList.addAll(photolist.map { it.toPhotoInfo() })
            }
    }


    fun onImageBitmapCaptured(bitmap: ImageBitmap) {
        val localDateTime = currentDateTime()
        viewModelScope.launch {
            val photoItem = PhotoItem().apply {
                datetime = localDateTime.toString()
                imageBase64 = encodeImageToBase64(bitmap) ?: ""
            }
            crackLogId.let {
                photoRepo.savePhotoItem(
                    photoItem = photoItem,
                    crackLogItemId = ObjectId.invoke(it),
                    crackItemId = crackId
                )
            }
        }
        println("bitmap captured and saved in DB")

    }

    fun deletePhotoItem(photoInfo: PhotoInfo) {
        photoInfo.dateTime?.let { photoRepo.deletePhotoItem(it) }
    }

    fun onCaptureCancelled() {
        _editCrackUIState.update { it.copy(showCamera = false) }
        println("Camera View was closed")
    }

    fun updateTitle(title: String) {
        _editCrackUIState.update {
            it.copy(
                title = title,
            )
        }
    }

    fun saveTitle(title: String) {
        viewModelScope.launch {
            photoRepo.updateCrackItem(
                crackId = crackId,
                crackLogId = crackLogId,
                title = title
            )
        }
        println("Title saved")
    }

    fun saveOrientation(orientation: String) {
        viewModelScope.launch {
            photoRepo.updateCrackItem(
                crackId = crackId,
                crackLogId = crackLogId,
                orientation = orientation
            )
        }
        println("Orientation saved")
    }

    fun saveWidth(width: String) {
        viewModelScope.launch {
            photoRepo.updateCrackItem(
                crackId = crackId,
                crackLogId = crackLogId,
                width = width
            )
        }
        println("Width saved")
    }


    fun saveLength(length: String) {
        viewModelScope.launch {
            photoRepo.updateCrackItem(
                crackId = crackId,
                crackLogId = crackLogId,
                length = length
            )
        }
        println("Length saved")
    }

    fun showCameraView() {
        _editCrackUIState.update { it.copy(showCamera = true) }
        println("Camera View opens")
    }

    private fun currentDateTime(): LocalDateTime {
        val currentMoment: Instant = Clock.System.now()
        return currentMoment.toLocalDateTime(TimeZone.UTC)
    }

}
