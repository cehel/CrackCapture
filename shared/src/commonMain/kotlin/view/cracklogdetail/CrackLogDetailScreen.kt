package view.cracklogdetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import view.ui.LazyColumnSwipeDismiss

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

        CrackList(viewModel.crackItems, crackLogObj, viewModel::removeCrackItem)
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
    crackLogId: ObjectId,
    removeItem: (Long) -> Unit
) {

    val navigator = LocalNavigator.currentOrThrow

    LazyColumnSwipeDismiss(items = crackItems,
        itemToKey = { crackItem -> crackItem.id.toString() },
        onDismiss = { crackItem -> removeItem(crackItem.id) },
        rowContent = { crackItem, itemIndex ->
            Surface(Modifier.fillMaxSize().clickable {
                navigator.push(
                    PhotoListScreen(
                        crackId = crackItem.id,
                        crackLogId = crackLogId.toHexString()
                    )
                )
            }) {
                CrackItemRow(crackItem, itemIndex)
            }
        })
}

@Composable
fun CrackItemRow(crackItem: CrackItem, index: Int) {
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
                    .padding(all = 16.dp)
            ) {

                Box(
                    modifier = Modifier.size(30.dp).align(Alignment.CenterVertically)
                ) {
                    Canvas(modifier = Modifier.size(30.dp), onDraw = {
                        drawCircle(
                            Color.Black.copy(alpha = 0.6f),
                            radius = 60f,
                            center = Offset(size.width / 2f, size.height / 2f)
                        )
                        drawCircle(
                            Color.White,
                            radius = 57f,
                            center = Offset(size.width / 2f, size.height / 2f)
                        )
                    })
                    Text(
                        text = "$index.",
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.height(40.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .align(Alignment.Center),

                        style = TextStyle(
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                        )
                    )


                }

                Column(Modifier.padding(start = 16.dp, end = 8.dp)) {
                    Text(
                        text = crackItem.title,
                        color = MaterialTheme.colors.onSurface,

                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = crackItem.orientation,
                        color = MaterialTheme.colors.onSurface,

                        style = TextStyle(fontSize = 16.sp)
                    )

                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.align(Alignment.Bottom).fillMaxSize()
                ) {
                    Text(
                        text = crackItem.length,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 8.dp),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
            }
        }
    }
}