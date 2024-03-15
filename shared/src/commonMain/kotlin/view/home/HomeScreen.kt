package view.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import view.ui.LazyColumnSwipeDismiss

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

        HomeContent(viewModel.crackLogItems, deleteCrackLog = viewModel::deleteCrackLog)
    }
}

@Composable
fun HomeContent(
    crackLogs: SnapshotStateList<CrackLogItem>,
    deleteCrackLog: (String) -> Unit
) {

    CrackLogReportList(crackLogs = crackLogs, deleteCrackLog = deleteCrackLog)

}

@Composable
fun CrackLogReportList(
    crackLogs: SnapshotStateList<CrackLogItem>,
    deleteCrackLog: (String) -> Unit
) {

    val navigator = LocalNavigator.currentOrThrow

    LazyColumnSwipeDismiss(items = crackLogs,
        itemToKey = { log -> log._id.toHexString() },
        onDismiss = { log -> deleteCrackLog(log._id.toHexString()) },
        rowContent = { crackLog, _ ->
            Surface(Modifier.clickable {
                navigator.push(CrackLogDetailScreen(crackLog._id.toHexString()))
            }) {
                CrackLogItemRow(crackLog)
            }
        })
}

@Composable
fun CrackLogItemRow(crackLogItem: CrackLogItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                Text(
                    text = crackLogItem.address ?: "",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(8.dp)
                )
                Column(horizontalAlignment = Alignment.End) {

                    Icon(
                        imageVector = Icons.Filled.Place,
                        "",
                        Modifier.wrapContentSize()
                    )

                    Text(
                        text = "Cracks:" + crackLogItem.cracks.size,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 8.dp),
                        style = TextStyle(fontSize = 16.sp, fontStyle = FontStyle.Italic)
                    )
                }
            }
        }
    }

}