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
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.materna.screens.cadastro.CadastroScreen
import com.example.materna.screens.cadastro.CadastroScreenViewModel
import com.example.materna.screens.campanhas.CampanhaScreen
import com.example.materna.screens.campanhas.CampanhasScreen
import com.example.materna.screens.confirmaremail.ConfirmarEmailScreen
import com.example.materna.screens.confirmaremail.ConfirmarEmailScreenViewModel
import com.example.materna.screens.dashboard.DashboardScreen
import com.example.materna.screens.dashboard.DashboardScreenViewModel
import com.example.materna.screens.jornada.JornadaScreen
import com.example.materna.screens.jornada.JornadaScreenViewModel
import com.example.materna.screens.login.LoginScreen
import com.example.materna.screens.login.LoginScreenViewModel
import com.example.materna.screens.onboarding.OnboardingScreen
import com.example.materna.screens.perfil.EditarPerfilScreen
import com.example.materna.screens.perfil.EditarPerfilScreenViewModel
import com.example.materna.screens.perfil.PerfilScreen
import com.example.materna.screens.perfil.PerfilScreenViewModel
import com.example.materna.screens.servicos.ServicosScreen
import com.example.materna.screens.servicos.ServicosScreenViewModel
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
                    "campanhas",
                    "campanha/{id}",
                    "jornada",
                    "jornada/{id_instituicao}",
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

                                val settings = withContext(Dispatchers.IO) {
                                    settingsDao.observeSettings().first()
                                }

                                val idDoadoraPendente = settings?.idDoadoraPendente

                                val destination = when {
                                    idDoadoraPendente != null && idDoadoraPendente > 0 && !settings.emailPendente.isNullOrBlank() -> {
                                        "confirmacao/$idDoadoraPendente"
                                    }
                                    settings?.onboardingCompleted == true -> {
                                        "login"
                                    }
                                    else -> {
                                        "onboarding"
                                    }
                                }

                                navController.navigate(destination) {
                                    popUpTo("splash") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
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
                            val loginScreenViewModel: LoginScreenViewModel = viewModel()

                            LoginScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                loginScreenViewModel = loginScreenViewModel
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
                            route = "confirmar-email/{id_doadora}",
                            arguments = listOf(
                                navArgument(name = "id_doadora") {
                                    type = NavType.IntType
                                }
                            )
                        ) {
                            val idDoadora = it.arguments?.getInt("id_doadora")

                            val confirmarEmailScreenViewModel: ConfirmarEmailScreenViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(
                                        modelClass: Class<T>
                                    ): T {
                                        return ConfirmarEmailScreenViewModel(settingsDao) as T
                                    }
                                }
                            )

                            ConfirmarEmailScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                idDoadora = idDoadora!!,
                                confirmarEmailScreenViewModel = confirmarEmailScreenViewModel
                            )
                        }





                        composable(route = "dashboard") {
                            val dashboardScreenViewModel: DashboardScreenViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(
                                        modelClass: Class<T>
                                    ): T {
                                        return DashboardScreenViewModel() as T
                                    }
                                }
                            )

                            DashboardScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                dashboardScreenViewModel = dashboardScreenViewModel
                            )
                        }

                        composable(route = "campanhas") {
                            val dashboardEntry = remember(it) {
                                navController.getBackStackEntry("dashboard")
                            }

                            val dashboardScreenViewModel: DashboardScreenViewModel = viewModel(
                                viewModelStoreOwner = dashboardEntry
                            )

                            CampanhasScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                dashboardScreenViewModel = dashboardScreenViewModel
                            )
                        }

                        composable(
                            route = "campanha/{id}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.IntType
                                }
                            )
                        ) { backStackEntry ->

                            val idCampanha = backStackEntry.arguments?.getInt("id")

                            val dashboardEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("dashboard")
                            }

                            val dashboardScreenViewModel: DashboardScreenViewModel = viewModel(
                                viewModelStoreOwner = dashboardEntry
                            )

                            val campanhas by dashboardScreenViewModel.campanhas
                                .observeAsState(emptyList())

                            val campanha = campanhas.find {
                                it.id == idCampanha
                            }

                            if (campanha != null) {
                                CampanhaScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    navController = navController,
                                    campanha = campanha
                                )
                            }
                        }





                        composable(route = "jornada") {
                            val jornadaScreenViewModel: JornadaScreenViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(
                                        modelClass: Class<T>
                                    ): T {
                                        return JornadaScreenViewModel() as T
                                    }
                                }
                            )

                            JornadaScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                jornadaScreenViewModel = jornadaScreenViewModel
                            )
                        }

                        composable(
                            route = "jornada/{id_instituicao}",
                            arguments = listOf(
                                navArgument("id_instituicao") {
                                    type = NavType.IntType
                                }
                            )
                            ) { backStackEntry ->

                            val idInstituicao = backStackEntry.arguments?.getInt("id_instituicao") ?: return@composable

                            val jornadaScreenViewModel: JornadaScreenViewModel = viewModel()

                            LaunchedEffect(idInstituicao) {
                                jornadaScreenViewModel.carregarInstituicao(idInstituicao)
                            }

                            JornadaScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                jornadaScreenViewModel = jornadaScreenViewModel
                            )
                        }





                        composable(route = "servicos") {
                            val servicosScreenViewModel: ServicosScreenViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(
                                        modelClass: Class<T>
                                    ): T {
                                        return ServicosScreenViewModel() as T
                                    }
                                }
                            )

                            ServicosScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                servicosScreenViewModel = servicosScreenViewModel,
                                onSelecionarBanco = { idInstituicao ->
                                    navController.navigate("jornada/$idInstituicao") {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }


                        composable(route = "perfil") {
                            val perfilScreenViewModel: PerfilScreenViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(
                                        modelClass: Class<T>
                                    ): T {
                                        return PerfilScreenViewModel(settingsDao) as T
                                    }
                                }
                            )

                            PerfilScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                perfilScreenViewModel = perfilScreenViewModel
                            )
                        }

                        composable(route = "editar_perfil") {
                            val editarPerfilScreenViewModel: EditarPerfilScreenViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(
                                        modelClass: Class<T>
                                    ): T {
                                        return EditarPerfilScreenViewModel(settingsDao) as T
                                    }
                                }
                            )

                            EditarPerfilScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                editarPerfilScreenViewModel = editarPerfilScreenViewModel
                            )
                        }

                    }
                }
            }
        }
    }
}