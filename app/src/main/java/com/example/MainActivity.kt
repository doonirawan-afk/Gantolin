package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.db.AppDatabase
import com.example.data.repository.OilReminderRepository
import com.example.ui.screens.VehicleListScreen
import com.example.ui.screens.VehicleDetailScreen
import com.example.ui.screens.OilTipsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.OilReminderViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize DB and Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = OilReminderRepository(database)

        setContent {
            MyApplicationTheme {
                // 2. Initialize ViewModel using Factory
                val viewModel: OilReminderViewModel by viewModels {
                    OilReminderViewModel.provideFactory(repository)
                }

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "vehicle_list"

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Only show bottom navigation on top-level destinations
                        if (currentRoute == "vehicle_list" || currentRoute == "oil_tips") {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 4.dp,
                                modifier = Modifier.testTag("main_navigation_bar")
                            ) {
                                // Vehicles List Tab
                                NavigationBarItem(
                                    selected = currentRoute == "vehicle_list",
                                    onClick = {
                                        if (currentRoute != "vehicle_list") {
                                            navController.navigate("vehicle_list") {
                                                popUpTo("vehicle_list") { inclusive = true }
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = "Kendaraan"
                                        )
                                    },
                                    label = { Text("Kendaraan") },
                                    modifier = Modifier.testTag("tab_vehicles")
                                )

                                // Oil Tips Tab
                                NavigationBarItem(
                                    selected = currentRoute == "oil_tips",
                                    onClick = {
                                        if (currentRoute != "oil_tips") {
                                            navController.navigate("oil_tips") {
                                                popUpTo("vehicle_list") { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Tips"
                                        )
                                    },
                                    label = { Text("Tips & Panduan") },
                                    modifier = Modifier.testTag("tab_tips")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "vehicle_list",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("vehicle_list") {
                            VehicleListScreen(
                                viewModel = viewModel,
                                onVehicleClick = { vehicle ->
                                    navController.navigate("vehicle_detail/${vehicle.id}")
                                }
                            )
                        }

                        composable(
                            route = "vehicle_detail/{vehicleId}",
                            arguments = listOf(navArgument("vehicleId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val vehicleId = backStackEntry.arguments?.getInt("vehicleId") ?: 0
                            VehicleDetailScreen(
                                vehicleId = vehicleId,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("oil_tips") {
                            OilTipsScreen()
                        }
                    }
                }
            }
        }
    }
}
