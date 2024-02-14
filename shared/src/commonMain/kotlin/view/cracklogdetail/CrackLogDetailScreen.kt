package view.cracklogdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.CrackItem
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import domain.realmConfigWithName
import io.realm.kotlin.Realm
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId
import view.navigation.ScreenKeys
import view.photolist.PhotoListScreen

class CrackLogDetailScreen(
    val crackLogId: String
) : Screen {


    override val key = ScreenKeys.CRACKLOGCREATION.name

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val crackLogObj = BsonObjectId.invoke(crackLogId)

        val viewModel = getViewModel(Unit, viewModelFactory {
            val realmConfig = realmConfigWithName("CrackLogDB")
            val realm = Realm.open(realmConfig)
            val photoItemRepository = PhotoItemRepository(realm)
            CrackLogDetailViewModel(
                crackLogObj,
                photoItemRepository
            )
        })

        val crackItemCreated by viewModel.crackItemCreated.collectAsState()

        crackItemCreated?.let {
            val crackId = it.id
            viewModel.resetCrackItem()
            navigator.push(
                PhotoListScreen(
                    crackLogId = crackLogId, crackId = crackId
                )
            )
        }

        CrackList(viewModel.crackItems, crackLogObj)
        Box(modifier = Modifier.fillMaxSize()) {
            FloatingActionButton(modifier = Modifier
                .padding(all = 16.dp)
                .align(alignment = Alignment.BottomEnd), onClick = {
                viewModel.addCrackItem()
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Defect")
            }
        }
    }
}

@Composable
fun CrackList(
    crackItems: SnapshotStateList<CrackItem>,
    crackLogId: ObjectId
) {

    val navigator = LocalNavigator.currentOrThrow
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(crackItems.size) { index ->
            val crackItem = crackItems[index]
            Surface(Modifier.clickable {
                navigator.push(
                    PhotoListScreen(
                        crackId = crackItem.id,
                        crackLogId = crackLogId.toHexString()
                    )
                )
            }) {
                CrackItemRow(crackItem)
            }
            Divider()
        }
    }
}

@Composable
fun CrackItemRow(crackItem: CrackItem) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = crackItem.description, style = MaterialTheme.typography.h6)
        Text(
            text = "Length: ${crackItem.length}",
            style = MaterialTheme.typography.body1
        )
        Text(
            text = "Orientation: ${crackItem.orientation}",
            style = MaterialTheme.typography.body2
        )
    }
}