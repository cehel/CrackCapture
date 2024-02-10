package view.photolist

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import data.CrackLogItem
import data.PhotoItem
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import encodeImageToBase64
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
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

class PhotoListScreenViewModel(val photoRepo: PhotoItemRepository) : ViewModel() {

    private val _showCameraView = MutableStateFlow(false)
    val showCameraView: StateFlow<Boolean> = _showCameraView

    private var photoId = 0

    var crackLogItemId: ObjectId? = null

    val photoInfoList: SnapshotStateList<PhotoInfo> = mutableStateListOf()

    val crackLogItems: SnapshotStateList<CrackLogItem> = mutableStateListOf()

    init {
        viewModelScope.launch {
            photoRepo.crackLogItems.collect { event: ResultsChange<CrackLogItem> ->
                when (event) {
                    is InitialResults -> {
                        if (event.list.isEmpty()) {
                            photoRepo.saveCrackLog("Test")
                        }
                        crackLogItemId = event.list.first()._id
                        listenForPhotos(crackLogId = event.list.first()._id, crackItemId = 0L)
                        crackLogItems.clear()
                        crackLogItems.addAll(event.list)
                    }

                    is UpdatedResults -> {
                        if (event.deletions.isNotEmpty() && crackLogItems.isNotEmpty()) {
                            event.deletions.reversed().forEach {
                                crackLogItems.removeAt(it)
                            }
                        }
                        if (event.insertions.isNotEmpty()) {
                            event.insertions.forEach {
                                crackLogItems.add(it, event.list[it])
                            }
                        }
                        if (event.changes.isNotEmpty()) {
                            event.changes.forEach {
                                crackLogItems.removeAt(it)
                                crackLogItems.add(it, event.list[it])
                            }
                        }
                    }

                    else -> Unit // No-op
                }

            }
        }
    }

    suspend fun listenForPhotos(crackLogId: ObjectId, crackItemId: Long) {
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
            crackLogItemId?.let {
                photoRepo.savePhotoItem(
                    photoItem = photoItem,
                    crackLogItemId = it,
                    crackItemId = 0L
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
