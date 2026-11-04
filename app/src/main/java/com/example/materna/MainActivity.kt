package com.example.materna

import android.annotation.SuppressLint
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.materna.components.MaternaBottomBar
import com.example.materna.data.local.MaternaDatabaseProvider
import com.example.materna.screens.EmDesenvolvimentoScreen
import com.example.materna.screens.cadastro.CadastroScreen
import com.example.materna.screens.cadastro.CadastroScreenViewModel
import com.example.materna.screens.confirmacao.ConfirmacaoScreen
import com.example.materna.screens.confirmacao.ConfirmacaoScreenViewModel
import com.example.materna.screens.dashboard.DashboardScreen
import com.example.materna.screens.dashboard.DashboardScreenViewModel
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
    @SuppressLint("ViewModelConstructorInComposable")
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

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val bottomBarRoutes = listOf(
                    "dashboard",
                    "jornada",
                    "servicos",
                    "perfil"
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute in bottomBarRoutes) {
                            MaternaBottomBar(
                                navController = navController
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        enterTransition = {
                            val fromIndex = bottomBarRoutes.indexOf(initialState.destination.route)
                            val toIndex = bottomBarRoutes.indexOf(targetState.destination.route)

                            val direction = if (fromIndex >= 0 && toIndex >= 0 && toIndex < fromIndex) {
                                AnimatedContentTransitionScope.SlideDirection.Right
                            } else {
                                AnimatedContentTransitionScope.SlideDirection.Left
                            }

                            slideIntoContainer(
                                towards = direction,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {
                            val fromIndex = bottomBarRoutes.indexOf(initialState.destination.route)
                            val toIndex = bottomBarRoutes.indexOf(targetState.destination.route)

                            val direction = if (fromIndex >= 0 && toIndex >= 0 && toIndex < fromIndex) {
                                AnimatedContentTransitionScope.SlideDirection.Right
                            } else {
                                AnimatedContentTransitionScope.SlideDirection.Left
                            }

                            slideOutOfContainer(
                                towards = direction,
                                animationSpec = tween(300)
                            )
                        },
                        popEnterTransition = {
                            val fromIndex = bottomBarRoutes.indexOf(initialState.destination.route)
                            val toIndex = bottomBarRoutes.indexOf(targetState.destination.route)

                            val direction = if (fromIndex >= 0 && toIndex >= 0 && toIndex < fromIndex) {
                                AnimatedContentTransitionScope.SlideDirection.Right
                            } else {
                                AnimatedContentTransitionScope.SlideDirection.Left
                            }

                            slideIntoContainer(
                                towards = direction,
                                animationSpec = tween(300)
                            )
                        },
                        popExitTransition = {
                            val fromIndex = bottomBarRoutes.indexOf(initialState.destination.route)
                            val toIndex = bottomBarRoutes.indexOf(targetState.destination.route)

                            val direction = if (fromIndex >= 0 && toIndex >= 0 && toIndex < fromIndex) {
                                AnimatedContentTransitionScope.SlideDirection.Right
                            } else {
                                AnimatedContentTransitionScope.SlideDirection.Left
                            }

                            slideOutOfContainer(
                                towards = direction,
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

                                val destination = if (onboardingCompleted) "login" else "onboarding"

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
                                modifier = Modifier.padding(innerPadding),
                                navController = navController
                            )
                        }

                        composable(route = "login") {
                            LoginScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                loginScreenViewModel = LoginScreenViewModel()
                            )
                        }

                        composable(route = "cadastro") {
                            CadastroScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                cadastroScreenViewModel = remember { CadastroScreenViewModel(settingsDao = settingsDao) }
                            )
                        }

                        composable(
                            route = "confirmacao/{id_doadora}",
                            arguments = listOf(
                                navArgument(name = "id_doadora") {
                                    type = NavType.IntType
                                }
                            )
                        ) {
                            val idDoadora = it.arguments?.getInt("id_doadora")

                            val confirmacaoScreenViewModel: ConfirmacaoScreenViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(
                                        modelClass: Class<T>
                                    ): T {
                                        return ConfirmacaoScreenViewModel(settingsDao) as T
                                    }
                                }
                            )

                            ConfirmacaoScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                idDoadora = idDoadora!!,
                                confirmacaoScreenViewModel = confirmacaoScreenViewModel
                            )
                        }

                        composable(route = "dashboard") {
                            val dashboardScreenViewModel: DashboardScreenViewModel = viewModel()

                            DashboardScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                dashboardScreenViewModel = dashboardScreenViewModel
                            )
                        }

                        composable(route = "jornada") {
                            EmDesenvolvimentoScreen(
                                titulo = "Minha Jornada",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        composable(route = "servicos") {
                            EmDesenvolvimentoScreen(
                                titulo = "Serviços",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        composable(route = "perfil") {
                            EmDesenvolvimentoScreen(
                                titulo = "Meu Perfil",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}