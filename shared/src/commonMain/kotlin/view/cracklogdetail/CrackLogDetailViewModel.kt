package view.cracklogdetail

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import data.CrackItem
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.PendingObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class CrackLogDetailViewModel(
    val crackLogId: ObjectId,
    val photoItemRepository: PhotoItemRepository
) : ViewModel() {

    val crackItems: SnapshotStateList<CrackItem>

    private val _showSpinner = MutableStateFlow(false)
    val showSpinner: StateFlow<Boolean> = _showSpinner


    private val _crackItemCreated = MutableStateFlow(null)
    val crackItemCreated: StateFlow<CrackItem?> = _crackItemCreated

    init {
        crackItems = mutableStateListOf()
        viewModelScope.launch {
            listenForCrackItemChanges()
        }
    }

    fun addCrackItem() {
        _showSpinner.value = true
        viewModelScope.launch(Dispatchers.IO) {
            photoItemRepository.createNewCrackItem(crackLogId)
            _showSpinner.emit(false)
        }
    }

    fun resetCrackItem() {
        _crackItemCreated.value = null
    }

    private suspend fun listenForCrackItemChanges() {
        photoItemRepository.crackLogItemFlow(crackLogId = crackLogId)
            .collect { singleQueryChange ->
                when (singleQueryChange) {
                    is DeletedObject -> {
                        crackItems.clear()
                    }

                    is InitialObject -> {
                        crackItems.clear()
                        crackItems.addAll(singleQueryChange.obj.cracks)
                    }

                    is UpdatedObject -> {
                        if (singleQueryChange.changedFields.contains("cracks")) {
                            crackItems.clear()
                            crackItems.addAll(singleQueryChange.obj.cracks)
                        }
                    }

                    is PendingObject -> {
                    }
                }
            }
    }


}