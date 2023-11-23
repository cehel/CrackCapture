import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory


// SharedModule.kt
@Composable
fun BerufswahlScreen() {
    val viewModel = getViewModel(Unit, viewModelFactory { BerufswahlViewModel() })

    // State to hold the text entered by the user
    var userText by remember { mutableStateOf("") }

    val recommendation by viewModel.recommendation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Text Field for user input
        TextField(
            value = userText,
            onValueChange = { userText = it },
            label = { Text("Enter Text") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Button to trigger ChatGPT with user input
        Button(
            onClick = {
                viewModel.askChatGPT(userText)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ask ChatGPT")
        }

        // Show spinner while loading
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .padding(16.dp)
            )
        }

        // Show recommendation or error
        recommendation?.let {
            Text("Recommendation: $it")
        }

        error?.let {
            Text("Error: $it")
        }
    }
}


expect fun getPlatformName(): String
