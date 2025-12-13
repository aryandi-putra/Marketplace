package com.aryandi.marketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aryandi.marketplace.presentation.login.LoginScreen
import com.aryandi.marketplace.presentation.products.ProductsScreen
import com.aryandi.marketplace.ui.theme.MarketplaceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarketplaceTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { token ->
                                navController.navigate("products") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("products") {
                        ProductsScreen()
                    }
                }
            }
        }
    }
}