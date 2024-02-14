package view.photolist

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import data.PhotoItem
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import encodeImageToBase64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val crackLogObjId = ObjectId.invoke(crackLogId)

    private val _showCameraView = MutableStateFlow(false)
    val showCameraView: StateFlow<Boolean> = _showCameraView

    val photoInfoList: SnapshotStateList<PhotoInfo> = mutableStateListOf()

    init {
        viewModelScope.launch {
            listenForPhotos(crackLogId = crackLogObjId, crackItemId = crackId)
        }
    }

    private suspend fun listenForPhotos(crackLogId: ObjectId, crackItemId: Long) {

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
        _showCameraView.value = false
        println("Camera View was closed")
    }

    fun showCameraView() {
        _showCameraView.value = true
        println("Camera View opens")
    }

    private fun currentDateTime(): LocalDateTime {
        val currentMoment: Instant = Clock.System.now()
        return currentMoment.toLocalDateTime(TimeZone.UTC)
    }

}
