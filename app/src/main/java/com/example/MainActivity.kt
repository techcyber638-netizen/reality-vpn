package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberTechTheme
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextSecondary
import com.example.viewmodel.VpnViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Shield)
    object ServerList : Screen("servers", "Servers", Icons.Default.Dns)
    object AddConfig : Screen("add_config", "Add Node", Icons.Default.Dns)
    object Stats : Screen("stats", "Stats", Icons.Default.BarChart)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: VpnViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CyberTechTheme {
                var isSplashActive by remember { mutableStateOf(true) }
                var showLogViewer by remember { mutableStateOf(false) }

                if (isSplashActive) {
                    SplashScreen(onSplashFinished = { 
                        if (isSplashActive) isSplashActive = false 
                    })
                } else {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    val bottomNavScreens = listOf(
                        Screen.Home,
                        Screen.ServerList,
                        Screen.Stats,
                        Screen.Settings
                    )

                    val showBottomBar = currentRoute in bottomNavScreens.map { it.route }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = CyberDarkBg,
                        bottomBar = {
                            if (showBottomBar) {
                                NavigationBar(
                                    containerColor = CyberCardBg,
                                    contentColor = CyberCyan,
                                    tonalElevation = 8.dp,
                                    modifier = Modifier
                                        .windowInsetsPadding(WindowInsets.navigationBars)
                                        .testTag("cyber_bottom_navigation")
                                ) {
                                    bottomNavScreens.forEach { screen ->
                                        val isSelected = currentRoute == screen.route
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = {
                                                if (currentRoute != screen.route) {
                                                    navController.navigate(screen.route) {
                                                        popUpTo(Screen.Home.route) { saveState = true }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = screen.icon,
                                                    contentDescription = screen.title,
                                                    tint = if (isSelected) CyberCyan else CyberTextMuted
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = screen.title,
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) CyberCyan else CyberTextSecondary
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                indicatorColor = CyberCyan.copy(alpha = 0.15f)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToServerList = { navController.navigate(Screen.ServerList.route) },
                                    onNavigateToAddConfig = { navController.navigate(Screen.AddConfig.route) },
                                    onOpenLogs = { showLogViewer = true }
                                )
                            }

                            composable(Screen.ServerList.route) {
                                ServerListScreen(
                                    viewModel = viewModel,
                                    onNavigateToAddConfig = { navController.navigate(Screen.AddConfig.route) },
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.AddConfig.route) {
                                AddServerScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Stats.route) {
                                StatisticsScreen(viewModel = viewModel)
                            }

                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onOpenLogs = { showLogViewer = true }
                                )
                            }
                        }

                        if (showLogViewer) {
                            LogViewerDialog(
                                viewModel = viewModel,
                                onDismiss = { showLogViewer = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
