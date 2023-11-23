import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import model.CrackReport


@Composable
fun CrackCaptureEntryScreen() {

    val viewModel = getViewModel(Unit, viewModelFactory { CrackCaptureEntryViewModel() })

    // State to hold the text entered by the user
    var userText by remember { mutableStateOf("") }

    val crackReports by viewModel.crackReports.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onAddClicked) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) {
        LazyColumn {
            items(crackReports) { report ->
                ReportItem(report)
            }
        }
    }
}

@Composable
fun ReportItem(report: CrackReport) {
    // Layout for each crack report item
    Card(elevation = 4.dp, modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = report.description, style = MaterialTheme.typography.h6)
            Text(text = "Location: ${report.place}", style = MaterialTheme.typography.body2)
            // Display images if available
            report.photos.forEach { photo ->
                Image(bitmap = photo, contentDescription = null)
            }
        }
    }
}
