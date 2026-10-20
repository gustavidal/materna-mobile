package com.example.materna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.materna.data.local.MaternaDatabaseProvider
import com.example.materna.screens.login.LoginScreen
import com.example.materna.screens.login.LoginScreenViewModel
import com.example.materna.screens.onboarding.OnboardingScreen
import com.example.materna.screens.splash.SplashScreen
import com.example.materna.ui.theme.MaternaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaternaTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val database = remember {
                    MaternaDatabaseProvider.getDatabase(context)
                }
                val settingsDao = remember {
                    database.appSettingsDao()
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        },
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        }
                    ) {
                        composable(route = "splash") {
                            var progress by remember {
                                mutableStateOf(0f)
                            }

                            LaunchedEffect(Unit) {
                                while (progress < 1f) {
                                    delay(30)
                                    progress = (progress + 0.01f).coerceAtMost(1f)
                                }
                                delay(200)

                                val onboardingCompleted = withContext(Dispatchers.IO) {
                                    settingsDao.observeSettings()
                                        .first()
                                        ?.onboardingCompleted == true
                                }

                                val destination = if (onboardingCompleted) {
                                    "login"
                                } else {
                                    "onboarding"
                                }

                                navController.navigate(destination) {
                                    popUpTo("splash") {
                                        inclusive = true
                                    }
                                }
                            }

                            SplashScreen(progress = progress)
                        }

                        composable(route = "onboarding") {
                            OnboardingScreen(
                                modifier = Modifier
                                    .padding(innerPadding),
                                navController = navController
                            )
                        }

                        composable(route = "login") {
                            LoginScreen(
                                modifier = Modifier
                                    .padding(innerPadding),
                                navController = navController,
                                loginScreenViewModel = LoginScreenViewModel()
                            )
                        }
                    }
                }
            }
        }
    }
}
