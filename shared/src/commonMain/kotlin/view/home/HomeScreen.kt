package view.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.CrackLogItem
import data.PhotoItemRepository
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import domain.realmConfigWithName
import io.realm.kotlin.Realm
import view.cracklogdetail.CrackLogDetailScreen
import view.navigation.ScreenKeys

class HomeScreen() : Screen {
    override val key = ScreenKeys.HOME.name

    @Composable
    override fun Content() {
        val viewModel = getViewModel(Unit, viewModelFactory {
            val realmConfig = realmConfigWithName("CrackLogDB")
            val realm = Realm.open(realmConfig)
            val photoItemRepository = PhotoItemRepository(realm)
            HomeScreenViewModel(
                photoItemRepository
            )
        })

        HomeContent(viewModel.crackLogItems)
    }
}

@Composable
fun HomeContent(
    crackLogs: SnapshotStateList<CrackLogItem>
) {

    CrackLogReportList(crackLogs = crackLogs)

}

@Composable
fun CrackLogReportList(crackLogs: SnapshotStateList<CrackLogItem>) {

    val navigator = LocalNavigator.currentOrThrow
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(crackLogs.size) { index ->
            val crackLog = crackLogs[index]
            Surface ( Modifier.clickable {
                navigator.push(CrackLogDetailScreen(crackLog._id.toHexString()))
            }) {
                CrackLogItemRow(crackLog)
            }
            Divider()
        }
    }
}

@Composable
fun CrackLogItemRow(crackLogItem: CrackLogItem) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = crackLogItem.name, style = MaterialTheme.typography.h6)
        Text(
            text = "Address: ${crackLogItem.address ?: ""}",
            style = MaterialTheme.typography.body1
        )
        Text(
            text = "Number of Items: ${crackLogItem.cracks.size}",
            style = MaterialTheme.typography.body2
        )
    }
}