package com.tinnomore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tinnomore.ui.navigation.AppNavigation
import com.tinnomore.ui.theme.TinNoMoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TinNoMoreTheme {
                AppNavigation()
            }
        }
    }
}
