import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import model.CrackReport
import kotlin.random.Random


class CrackCaptureEntryViewModel : ImageHandler, ViewModel() {

    private val _crackReports = MutableStateFlow<List<CrackReport>>(listOf())
    val crackReports: StateFlow<List<CrackReport>> = _crackReports

    fun onAddClicked() {
        val report = CrackReport(
            description = "Description ${Random.nextInt(0, 100)}",
            photos = listOf(),
            place = "Place ${Random.nextInt(0, 100)}"
        )
        val newList = mutableListOf<CrackReport>()
        newList.addAll(_crackReports.value)
        newList.add(report)
        _crackReports.value = newList
    }

    override fun onImageBytesCaptured(bytes: ByteArray?) {

    }
}
