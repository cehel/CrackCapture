package view.photolist

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import data.PhotoItem
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
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

class PhotoListScreenViewModel(val photoRepo: PhotoItemRepository) : ViewModel() {

    private val _showCameraView = MutableStateFlow(false)
    val showCameraView: StateFlow<Boolean> = _showCameraView

    private var photoId = 0

    val photoInfoList: SnapshotStateList<PhotoInfo> = mutableStateListOf()

    init {
        viewModelScope.launch {
            photoRepo.photoItems
                .collect { event: ResultsChange<PhotoItem> ->
                    when (event) {
                        is InitialResults -> {
                            photoInfoList.clear()
                            photoInfoList.addAll(event.list.map { it.toPhotoInfo() })
                        }

                        is UpdatedResults -> {
                            if (event.deletions.isNotEmpty() && photoInfoList.isNotEmpty()) {
                                event.deletions.reversed().forEach {
                                    photoInfoList.removeAt(it)
                                }
                            }
                            if (event.insertions.isNotEmpty()) {
                                event.insertions.forEach {
                                    photoInfoList.add(it, event.list[it].toPhotoInfo())
                                }
                            }
                            if (event.changes.isNotEmpty()) {
                                event.changes.forEach {
                                    photoInfoList.removeAt(it)
                                    photoInfoList.add(it, event.list[it].toPhotoInfo())
                                }
                            }
                        }

                        else -> Unit // No-op
                    }
                }
        }
    }


    fun onImageBitmapCaptured(bitmap: ImageBitmap) {
        val localDateTime = currentDateTime()
        photoRepo.savePhotoItem(image = bitmap, localDateTime = localDateTime)
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
