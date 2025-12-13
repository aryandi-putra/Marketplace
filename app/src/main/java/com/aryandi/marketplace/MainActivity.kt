package com.aryandi.marketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aryandi.marketplace.presentation.login.LoginScreen
import com.aryandi.marketplace.ui.theme.MarketplaceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarketplaceTheme {
                LoginScreen(
                    onLoginSuccess = { token ->

                    }
                )
            }
        }
    }
}