import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


private const val API_KEY = "s"

// BerufswahlViewModel.kt
class BerufswahlViewModel : ViewModel() {

    private val _recommendation = MutableStateFlow<String?>(null)
    val recommendation: StateFlow<String?> = _recommendation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val mChatGPTPromptService =
        ChatGPTPromptService(API_KEY, "https://api.openai.com/v1/chat/completions")

    fun askChatGPT(prompt: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = mChatGPTPromptService.getChatCompletion(prompt)
                _recommendation.value = response
            } catch (e: Exception) {
                _error.value = "Error happened: $e"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        mChatGPTPromptService.close()
    }
}

