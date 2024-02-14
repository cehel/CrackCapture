package view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import view.cracklogcreation.CrackLogCreationScreen
import view.home.HomeScreen
import view.navigation.ScreenKeys

@Composable
fun App() {
    MaterialTheme {
        Navigator(screen = HomeScreen()) { navigator ->
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = getTopBarTitleFromKey(navigator.lastItem.key)) },
                        backgroundColor = MaterialTheme.colors.primary,

                        navigationIcon = {
                            if (navigator.canPop) {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        },
                    )
                },
                floatingActionButton = {
                    getFloatingActionButtonFromKey(navigator.lastItem.key, navigator)
                }
            ) { innerPadding ->
                SlideTransition(
                    navigator = navigator,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

fun getTopBarTitleFromKey(key: ScreenKey) =
    when (ScreenKeys.fromString(key)) {
        ScreenKeys.HOME -> "Home"
        ScreenKeys.CRACKLOGDETAIL -> "Report Details"
        ScreenKeys.CRACKLOGCREATION -> "Report Creation"
        ScreenKeys.CRACKITEM -> "Crack / Defect"
        ScreenKeys.UNKNOWN -> ""
    }

@Composable
fun getFloatingActionButtonFromKey(key: String, navigator: Navigator) {
    when (ScreenKeys.fromString(key)) {
        ScreenKeys.HOME -> FloatingActionButton(onClick = {
            navigator.push(CrackLogCreationScreen())
        }) {
            Icon(Icons.Filled.Add, contentDescription = "Add Report")
        }

        else -> {}
    }
}

