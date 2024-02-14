package view.cracklogcreation

import data.PhotoItemRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrackLogCreationViewModel(val photoRepo: PhotoItemRepository) : ViewModel() {

    private val _showSpinner = MutableStateFlow(false)
    val showSpinner: StateFlow<Boolean> = _showSpinner

    fun saveCrackLogItem(
        name: String,
        address: String,
        navigate: () -> Unit
    ) {
        _showSpinner.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                photoRepo.saveCrackLog(logname = name, addr = address)
            }
            _showSpinner.emit(false)
            withContext(Dispatchers.Main) {
                navigate()
            }
        }

    }
}