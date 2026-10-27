package com.github.jetbrains.rssreader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.jetbrains.rssreader.app.FeedSideEffect
import com.github.jetbrains.rssreader.app.FeedStore
import com.github.jetbrains.rssreader.ui.AppTheme
import com.github.jetbrains.rssreader.ui.FeedListScreen
import com.github.jetbrains.rssreader.ui.MainScreen
import com.github.jetbrains.rssreader.ui.RssFeedAppBar
import com.github.jetbrains.rssreader.ui.Screen
import com.github.jetbrains.rssreader.ui.SignUpScreen
import kotlinx.coroutines.flow.filterIsInstance
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private enum class Tab(val screen: Screen, val label: StringResource, val icon: ImageVector) {
    Feed(Screen.Main, Res.string.tab_feed, Icons.Filled.Home),
    SignUp(Screen.SignUp, Res.string.tab_sign_up, Icons.Filled.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssReaderApp(navController: NavHostController = rememberNavController()) {
    AppTheme {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = Screen.valueOf(
            backStackEntry?.destination?.route ?: Screen.Main.name
        )
        val snackbarHostState = remember { SnackbarHostState() }

        fun selectTab(tab: Tab) {
            navController.navigate(tab.screen.name) {
                popUpTo(navController.graph.findStartDestination().route ?: Screen.Main.name) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                RssFeedAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = currentScreen == Screen.FeedList,
                    navigateUp = { navController.navigateUp() }
                )
            },
            bottomBar = {
                if (currentScreen != Screen.FeedList) {
                    NavigationBar {
                        Tab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = currentScreen == tab.screen,
                                onClick = { selectTab(tab) },
                                icon = { Icon(tab.icon, contentDescription = null) },
                                label = { Text(stringResource(tab.label)) }
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Main.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(route = Screen.Main.name) {
                    MainScreen(
                        onEditClick = { navController.navigate(Screen.FeedList.name) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
                composable(route = Screen.FeedList.name) {
                    FeedListScreen()
                }
                composable(route = Screen.SignUp.name) {
                    SignUpScreen(
                        onBack = { selectTab(Tab.Feed) },
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            val store: FeedStore = koinInject<FeedStore>()
            val error = store.observeSideEffect()
                .filterIsInstance<FeedSideEffect.Error>()
                .collectAsState(null)
            LaunchedEffect(error.value) {
                error.value?.let {
                    snackbarHostState.showSnackbar(
                        it.error.message.toString()
                    )
                }
            }
        }
    }
}
