package com.adriano.sharetheimage.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.adriano.sharetheimage.ui.navigation.ShareImageNavDisplay
import com.adriano.sharetheimage.ui.theme.ShareTheImageTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShareTheImageTheme {
                ShareImageNavDisplay()
            }
        }
    }
}