package com.aryandi.marketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aryandi.marketplace.presentation.cart.CartScreen
import com.aryandi.marketplace.presentation.login.LoginScreen
import com.aryandi.marketplace.presentation.productdetail.ProductDetailScreen
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
                        ProductsScreen(
                            onProductClick = { productId ->
                                navController.navigate("product/$productId")
                            },
                            onCartClick = {
                                navController.navigate("cart")
                            }
                        )
                    }

                    composable(
                        route = "product/{productId}",
                        arguments = listOf(
                            navArgument("productId") {
                                type = NavType.IntType
                            }
                        )
                    ) { backStackEntry ->
                        val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                        ProductDetailScreen(
                            productId = productId,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("cart") {
                        CartScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}