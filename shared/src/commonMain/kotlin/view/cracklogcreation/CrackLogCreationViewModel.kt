package view.cracklogcreation

import data.PhotoItemRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CrackLogCreationViewModel(val photoRepo: PhotoItemRepository) : ViewModel() {

    private val _showSpinner = MutableStateFlow(false)
    val showSpinner: StateFlow<Boolean> = _showSpinner

    fun saveCrackLogItem(
        name: String,
        address: String,
        navigate: () -> Unit
    ) {
        println("Save Crack Log start")
        _showSpinner.value = true
        viewModelScope.launch {
            println("Save Crack Log start in coroutinescope")
            photoRepo.saveCrackLog(logname = name, addr = address)
            println("Save Crack Log in repo")
            _showSpinner.emit(false)

            navigate()

        }

    }
}