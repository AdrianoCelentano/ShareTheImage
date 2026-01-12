package com.adriano.sharetheimage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShareImageNavDisplay(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}