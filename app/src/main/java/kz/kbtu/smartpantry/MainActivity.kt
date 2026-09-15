package kz.kbtu.smartpantry

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dagger.hilt.android.AndroidEntryPoint
import kz.kbtu.smartpantry.core.model.AppLanguage
import kz.kbtu.smartpantry.core.model.AppThemeMode
import kz.kbtu.smartpantry.core.ui.LocalAppLanguage
import kz.kbtu.smartpantry.core.ui.SmartPantryTheme
import kz.kbtu.smartpantry.core.ui.tr
import kz.kbtu.smartpantry.feature.analytics.ANALYTICS_ROUTE
import kz.kbtu.smartpantry.feature.analytics.AnalyticsScreen
import kz.kbtu.smartpantry.feature.assistant.ASSISTANT_ROUTE
import kz.kbtu.smartpantry.feature.assistant.AssistantScreen
import kz.kbtu.smartpantry.feature.auth.AUTH_ROUTE
import kz.kbtu.smartpantry.feature.auth.AuthScreen
import kz.kbtu.smartpantry.feature.inventory.INVENTORY_DETAIL_ROUTE
import kz.kbtu.smartpantry.feature.inventory.INVENTORY_ROUTE
import kz.kbtu.smartpantry.feature.inventory.InventoryDetailScreen
import kz.kbtu.smartpantry.feature.inventory.InventoryScreen
import kz.kbtu.smartpantry.feature.profile.PROFILE_ROUTE
import kz.kbtu.smartpantry.feature.profile.ProfileScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        enableEdgeToEdge()
        setContent {
            SmartPantryAppRoot()
        }
    }
}

private data class BottomDestination(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit,
)

@Composable
private fun SmartPantryAppRoot(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val session by viewModel.session.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(session?.email) {
        session?.email?.let(viewModel::ensureDemoData)
    }

    LaunchedEffect(session) {
        val isGraphReady = runCatching { navController.graph }.isSuccess
        if (!isGraphReady) return@LaunchedEffect

        if (session == null && currentDestination?.route != AUTH_ROUTE) {
            navController.navigate(AUTH_ROUTE) {
                // Avoid touching navController.graph before NavHost sets it.
                popUpTo(AUTH_ROUTE) { inclusive = true }
                launchSingleTop = true
            }
        } else if (session != null && currentDestination?.route == AUTH_ROUTE) {
            navController.navigate(INVENTORY_ROUTE) {
                popUpTo(AUTH_ROUTE) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    CompositionLocalProvider(
        LocalAppLanguage provides (profile?.appLanguage ?: AppLanguage.ENGLISH),
    ) {
        SmartPantryTheme(themeMode = profile?.themeMode ?: AppThemeMode.SYSTEM) {
            val bottomDestinations = listOf(
                BottomDestination(
                    route = INVENTORY_ROUTE,
                    label = tr("Inventory", "Запасы"),
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                ),
                BottomDestination(
                    route = ASSISTANT_ROUTE,
                    label = tr("Assistant", "Ассистент"),
                    icon = { Icon(Icons.Default.SmartToy, contentDescription = null) },
                ),
                BottomDestination(
                    route = ANALYTICS_ROUTE,
                    label = tr("Analytics", "Аналитика"),
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                ),
                BottomDestination(
                    route = PROFILE_ROUTE,
                    label = tr("Profile", "Профиль"),
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                ),
            )

            val showBottomBar = session != null && currentDestination?.route != AUTH_ROUTE

            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        NavigationBar {
                            bottomDestinations.forEach { destination ->
                                NavigationBarItem(
                                    selected = currentDestination
                                        ?.hierarchy
                                        ?.any { it.route == destination.route } == true,
                                    onClick = {
                                        navController.navigate(destination.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            restoreState = true
                                            launchSingleTop = true
                                        }
                                    },
                                    icon = destination.icon,
                                    label = { Text(destination.label) },
                                )
                            }
                        }
                    }
                },
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = AUTH_ROUTE,
                    modifier = Modifier.padding(innerPadding),
                ) {
                    composable(AUTH_ROUTE) {
                        AuthScreen()
                    }
                    composable(INVENTORY_ROUTE) {
                        InventoryScreen(
                            onItemOpen = { itemId ->
                                navController.navigate("inventory/$itemId")
                            },
                        )
                    }
                    composable(
                        route = INVENTORY_DETAIL_ROUTE,
                        arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "smartpantry://inventory/{itemId}" },
                        ),
                    ) {
                        InventoryDetailScreen()
                    }
                    composable(
                        route = ASSISTANT_ROUTE,
                        deepLinks = listOf(navDeepLink { uriPattern = "smartpantry://assistant" }),
                    ) {
                        AssistantScreen()
                    }
                    composable(ANALYTICS_ROUTE) {
                        AnalyticsScreen()
                    }
                    composable(PROFILE_ROUTE) {
                        ProfileScreen()
                    }
                }
            }
        }
    }
}
