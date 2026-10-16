package com.example.materna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.materna.screen.splash.SplashScreen
import com.example.materna.ui.theme.MaternaTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaternaTheme {
                var progress by remember { mutableStateOf(0f) }
                var isFinishedLoading by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    while (progress < 1f) {
                        delay(30) // Velocidade do carregamento
                        progress += 0.02f
                    }
                    delay(200) // Pequena pausa ao chegar em 100%
                    isFinishedLoading = true
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (!isFinishedLoading) {
                        SplashScreen(progress = progress)
                    } else {
                        HomeScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}



@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

}