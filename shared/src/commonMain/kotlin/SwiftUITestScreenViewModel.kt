import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SwiftUITestScreenViewModel : ViewModel(), ImageHandler {
    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes: StateFlow<ByteArray?> = _imageBytes

    override fun onImageBytesCaptured(byteArray: ByteArray?) {
        _imageBytes.value = byteArray
        println("Image bytearray is captured size: ${byteArray?.size}")
    }

}
