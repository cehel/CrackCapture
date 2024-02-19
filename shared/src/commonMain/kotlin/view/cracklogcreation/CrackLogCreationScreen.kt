package view.cracklogcreation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import domain.realmConfigWithName
import io.realm.kotlin.Realm
import view.navigation.ScreenKeys
import view.photolist.PhotoListScreen

class CrackLogCreationScreen : Screen {

    override val key = ScreenKeys.CRACKLOGCREATION.name

    @Composable
    override fun Content() {
        val viewModel = getViewModel(Unit, viewModelFactory {
            val realmConfig = realmConfigWithName("CrackLogDB")
            val realm = Realm.open(realmConfig)
            val photoItemRepository = PhotoItemRepository(realm)
            CrackLogCreationViewModel(
                photoItemRepository
            )
        })
        CreateCrackLog(viewModel)
    }
}

@Composable
fun CreateCrackLog(viewModel: CrackLogCreationViewModel) {
    val name = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val roomToAdd = remember { mutableStateOf("") }
    val itemList = remember { mutableStateListOf<String>() }

    val navigator = LocalNavigator.currentOrThrow


    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = address.value,
            onValueChange = { address.value = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        Row {
            TextField(
                value = roomToAdd.value,
                onValueChange = { roomToAdd.value = it },
                label = { Text("Room") },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = {
                    if (roomToAdd.value.isNotBlank()) {
                        itemList.add(roomToAdd.value)
                        roomToAdd.value = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Add")
            }
        }

        LazyColumn {
            items(items = itemList, itemContent = { item ->
                RoomsList(item = item) { itemList.remove(item) }
            })
        }

        Button(
            onClick = {
                println("Clicked save")
                viewModel.saveCrackLogItem(
                    name = name.value,
                    address = address.value
                ) {
                    navigator.pop()
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Create")
        }
    }
}

@Composable
fun RoomsList(item: String, onRemove: () -> Unit) {
    Row(modifier = Modifier.padding(8.dp)) {
        Text(text = item, style = MaterialTheme.typography.body1)
        IconButton(onClick = onRemove) {
            // Replace with your desired icon
            Icon(Icons.Filled.Delete, contentDescription = "Remove")
        }
    }
}