package com.app.exwk06

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Scaffold(modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.Center

        ) {
            composable("main") {
                MainScreen(
                    onNavigateToNoStockScreen = { navController.navigate("nostock") }
                )
            }
            composable("nostock"){
                NoStockScreen(
                    onBack = { navController.popBackStack()}

                )
            }
        }
    }

}