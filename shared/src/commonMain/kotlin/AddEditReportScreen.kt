import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.CrackReport
import kotlin.reflect.KProperty

@Composable
fun AddEditReportScreen(
    report: CrackReport?,
    onReportSave: (CrackReport) -> Unit
) {
    var description by remember { mutableStateOf(report?.description ?: "") }
    var place by remember { mutableStateOf(report?.place ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        TextField(
            value = place,
            onValueChange = { place = it },
            label = { Text("Place") }
        )
        Button(onClick = {
            // Logic to take or select a photo
            // ...
            onReportSave(CrackReport(description, place, listOf()))
        }) {
            Text("Save")
        }
    }
}

