package view.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import data.CrackLogItem
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.launch

class HomeScreenViewModel(val photoRepo: PhotoItemRepository) : ViewModel() {

    val crackLogItems: SnapshotStateList<CrackLogItem> = mutableStateListOf()

    init {
        viewModelScope.launch {
            photoRepo.crackLogItems.collect { event: ResultsChange<CrackLogItem> ->
                when (event) {
                    is InitialResults -> {
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
}

