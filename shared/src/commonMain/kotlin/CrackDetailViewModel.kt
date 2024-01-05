import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CrackDetailViewModel : ViewModel(), ImageHandler {
    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes: StateFlow<ByteArray?> = _imageBytes

    private val _showCameraView = MutableStateFlow<Boolean>(false)
    val showCameraView: StateFlow<Boolean> = _showCameraView

    override fun onImageBytesCaptured(byteArray: ByteArray?) {
        _imageBytes.value = byteArray
        println("Image bytearray is captured size: ${byteArray?.size}")
        _showCameraView.value = false
    }

    override fun onCancelled() {
        _showCameraView.value = false
        println("Camera View was closed")
    }

    fun showCameraView() {
        _showCameraView.value = true
    }

}
