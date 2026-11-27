package com.example.materna

import android.R.attr.tag
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import com.example.materna.models.domain.JornadaStatus
import com.example.materna.screens.agendamento.AgendamentoScreen
import com.example.materna.screens.cadastro.CadastroScreen
import com.example.materna.screens.cadastro.CadastroScreenViewModel
import com.example.materna.screens.campanhas.CampanhaScreen
import com.example.materna.screens.campanhas.CampanhasScreen
import com.example.materna.screens.confirmarbanco.ConfirmarBancoEstado
import com.example.materna.screens.confirmarbanco.ConfirmarBancoScreen
import com.example.materna.screens.confirmarbanco.ConfirmarBancoScreenViewModel
import com.example.materna.screens.confirmaremail.ConfirmarEmailScreen
import com.example.materna.screens.confirmaremail.ConfirmarEmailScreenViewModel
import com.example.materna.screens.dashboard.DashboardScreen
import com.example.materna.screens.dashboard.DashboardScreenViewModel
import com.example.materna.screens.editarperfil.EditarPerfilScreen
import com.example.materna.screens.editarperfil.EditarPerfilScreenViewModel
import com.example.materna.screens.jornada.JornadaEstado
import com.example.materna.screens.jornada.JornadaScreen
import com.example.materna.screens.jornada.JornadaScreenViewModel
import com.example.materna.screens.login.LoginScreen
import com.example.materna.screens.login.LoginScreenViewModel
import com.example.materna.screens.onboarding.OnboardingScreen
import com.example.materna.screens.others.ConhecaDoacaoScreen
import com.example.materna.screens.others.OrientacoesScreen
import com.example.materna.screens.others.QuestionarioScreen
import com.example.materna.screens.perfil.PerfilScreen
import com.example.materna.screens.perfil.PerfilScreenViewModel
import com.example.materna.screens.recuperarsenha.RecuperarSenhaScreen
import com.example.materna.screens.servicos.ServicosScreen
import com.example.materna.screens.servicos.ServicosScreenViewModel
import com.example.materna.screens.splash.SplashScreen
import com.example.materna.screens.splash.SplashScreenViewModel
import com.example.materna.session.SessionEvents
import com.example.materna.ui.theme.MaternaTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.light(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT,
                ),
            navigationBarStyle =
                SystemBarStyle.light(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT,
                ),
        )
        setContent {
            MaternaTheme {
                val navController = rememberNavController()

                val sessaoExpirada by SessionEvents.sessaoExpirada.collectAsState()
                val logoutRealizado by SessionEvents.logoutRealizado.collectAsState()

                val context = LocalContext.current

                val servicosCompartilhado: ServicosScreenViewModel =
                    viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory =
                            object : ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(
                                    modelClass: Class<T>,
                                ): T {
                                    return ServicosScreenViewModel(
                                        context = context.applicationContext,
                                    )
                                        as T
                                }
                            },
                    )

                val database = remember {
                    MaternaDatabaseProvider.getDatabase(context)
                }
                val settingsDao = remember {
                    database.appSettingsDao()
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                var mostrarAvisoSaidaJornada by remember {
                    mutableStateOf(false)
                }

                var navegacaoPendente by remember {
                    mutableStateOf<(() -> Unit)?>(null)
                }

                LaunchedEffect(sessaoExpirada, logoutRealizado) {
                    if (sessaoExpirada || logoutRealizado) {

                        Log.d("MaternaLogout", "Encerrando navegação autenticada")

                        servicosCompartilhado.limparDadosSessao()

                        mostrarAvisoSaidaJornada = false
                        navegacaoPendente = null

                        navController.navigate("login") {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }

                        SessionEvents.consumirEvento()

                        Log.i("MaternaLogout", "Redirecionamento ao login concluído")
                    }
                }

                val bottomBarRoutes =
                    listOf(
                        "dashboard",
                        "campanhas",
                        "campanha/{id}",
                        "jornada",
                        "jornada/{id_instituicao}",
                        "agendamento/{id_instituicao}/{id_jornada}/{id_etapa}/{id_coleta}/{id_doadora}/{id_jornada_doacao}?nome_instituicao={nome_instituicao}",
                        "servicos",
                        "confirmar_banco/{id_instituicao}",
                        "perfil",
                    )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = colorResource(R.color.background_cream),
                    bottomBar = {
                        if (currentRoute in bottomBarRoutes) {

                            MaternaBottomBar(
                                navController = navController,
                                onNavigateRequest = { _, navegar ->
                                    if (currentRoute == "jornada/{id_instituicao}") {
                                        val jornadaEntry = navController.currentBackStackEntry

                                        if (jornadaEntry != null) {
                                            val jornadaViewModel =
                                                ViewModelProvider(
                                                    jornadaEntry,
                                                )[JornadaScreenViewModel::class.java]

                                            val primeiraConcluida =
                                                jornadaViewModel.etapas.value
                                                    ?.firstOrNull {
                                                        it.id == "conheca_doacao"
                                                    }
                                                    ?.status == JornadaStatus.CONCLUIDA

                                            val jornadaCarregada =
                                                jornadaViewModel.estado.value ==
                                                    JornadaEstado.COM_BANCO

                                            if (jornadaCarregada && !primeiraConcluida) {
                                                navegacaoPendente = navegar
                                                mostrarAvisoSaidaJornada = true
                                            } else {
                                                navegar()
                                            }
                                        } else {
                                            navegar()
                                        }
                                    } else {
                                        navegar()
                                    }
                                },
                            )
                        }
                    },
                ) { innerPadding ->
                    if (mostrarAvisoSaidaJornada) {
                        AlertDialog(
                            onDismissRequest = {
                                mostrarAvisoSaidaJornada = false
                                navegacaoPendente = null
                            },
                            title = {
                                Text("Deseja sair da jornada?")
                            },
                            text = {
                                Text(
                                    "Você ainda não concluiu a primeira etapa. " +
                                        "Sua jornada ainda não está vinculada " +
                                        "à instituição selecionada.",
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        val navegar = navegacaoPendente

                                        mostrarAvisoSaidaJornada = false
                                        navegacaoPendente = null

                                        navegar?.invoke()
                                    },
                                ) {
                                    Text(
                                        text = "Sair mesmo assim",
                                        color = colorResource(R.color.main_pink),
                                    )
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        mostrarAvisoSaidaJornada = false
                                        navegacaoPendente = null
                                    },
                                ) {
                                    Text("Continuar jornada")
                                }
                            },
                        )
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding),
                        enterTransition = {
                            val fromIndex = bottomBarRoutes.indexOf(initialState.destination.route)
                            val toIndex = bottomBarRoutes.indexOf(targetState.destination.route)

                            val direction =
                                if (fromIndex >= 0 && toIndex >= 0 && toIndex < fromIndex) {
                                    AnimatedContentTransitionScope.SlideDirection.Right
                                } else {
                                    AnimatedContentTransitionScope.SlideDirection.Left
                                }

                            slideIntoContainer(
                                towards = direction,
                                animationSpec = tween(300),
                            )
                        },
                        exitTransition = {
                            val fromIndex = bottomBarRoutes.indexOf(initialState.destination.route)
                            val toIndex = bottomBarRoutes.indexOf(targetState.destination.route)

                            val direction =
                                if (fromIndex >= 0 && toIndex >= 0 && toIndex < fromIndex) {
                                    AnimatedContentTransitionScope.SlideDirection.Right
                                } else {
                                    AnimatedContentTransitionScope.SlideDirection.Left
                                }

                            slideOutOfContainer(
                                towards = direction,
                                animationSpec = tween(300),
                            )
                        },
                        popEnterTransition = {
                            val fromIndex = bottomBarRoutes.indexOf(initialState.destination.route)
                            val toIndex = bottomBarRoutes.indexOf(targetState.destination.route)

                            val direction =
                                if (fromIndex >= 0 && toIndex >= 0 && toIndex < fromIndex) {
                                    AnimatedContentTransitionScope.SlideDirection.Right
                                } else {
                                    AnimatedContentTransitionScope.SlideDirection.Left
                                }

                            slideIntoContainer(
                                towards = direction,
                                animationSpec = tween(300),
                            )
                        },
                        popExitTransition = {
                            val fromIndex = bottomBarRoutes.indexOf(initialState.destination.route)
                            val toIndex = bottomBarRoutes.indexOf(targetState.destination.route)

                            val direction =
                                if (fromIndex >= 0 && toIndex >= 0 && toIndex < fromIndex) {
                                    AnimatedContentTransitionScope.SlideDirection.Right
                                } else {
                                    AnimatedContentTransitionScope.SlideDirection.Left
                                }

                            slideOutOfContainer(
                                towards = direction,
                                animationSpec = tween(300),
                            )
                        },
                    ) {
                        composable(route = "splash") {
                            val splashScreenViewModel: SplashScreenViewModel =
                                viewModel(
                                    factory =
                                        object : ViewModelProvider.Factory {
                                            @Suppress("UNCHECKED_CAST")
                                            override fun <T : ViewModel> create(
                                                modelClass: Class<T>,
                                            ): T {
                                                return SplashScreenViewModel(
                                                    settingsDao = settingsDao,
                                                    context = context.applicationContext,
                                                )
                                                    as T
                                            }
                                        },
                                )

                            var progress by remember {
                                mutableStateOf(0f)
                            }

                            LaunchedEffect(Unit) {
                                while (progress < 1f) {
                                    delay(30)
                                    progress = (progress + 0.01f).coerceAtMost(1f)
                                }

                                delay(200)

                                val destination =
                                    try {
                                        splashScreenViewModel.verificarDestino()
                                    } catch (e: Exception) {
                                        Log.e("SplashScreen", "Erro ao verificar destino", e)
                                        "login"
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
                                modifier = Modifier,
                                navController = navController,
                            )
                        }

                        composable(route = "login") {
                            val loginScreenViewModel: LoginScreenViewModel = viewModel()

                            LoginScreen(
                                modifier = Modifier,
                                navController = navController,
                                loginScreenViewModel = loginScreenViewModel,
                            )
                        }

                        composable(route = "recuperar-senha") {
                            val appSettings by
                                remember(settingsDao) {
                                        settingsDao.observeSettings()
                                    }
                                    .collectAsState(initial = null)

                            RecuperarSenhaScreen(
                                emailCadastrado = appSettings?.emailDoadora,
                                onVoltarLogin = {
                                    navController.popBackStack()
                                },
                                onEnviarCodigo = { email ->
                                    // Próxima etapa: integrar o envio do OTP.
                                },
                            )
                        }

                        composable(route = "cadastro") {
                            CadastroScreen(
                                modifier = Modifier,
                                navController = navController,
                                cadastroScreenViewModel =
                                    remember { CadastroScreenViewModel(settingsDao = settingsDao) },
                            )
                        }

                        composable(
                            route = "confirmar-email/{id_doadora}",
                            arguments =
                                listOf(
                                    navArgument(name = "id_doadora") {
                                        type = NavType.IntType
                                    },
                                ),
                        ) {
                            val idDoadora = it.arguments?.getInt("id_doadora")

                            val confirmarEmailScreenViewModel: ConfirmarEmailScreenViewModel =
                                viewModel(
                                    factory =
                                        object : ViewModelProvider.Factory {
                                            @Suppress("UNCHECKED_CAST")
                                            override fun <T : ViewModel> create(
                                                modelClass: Class<T>,
                                            ): T {
                                                return ConfirmarEmailScreenViewModel(settingsDao)
                                                    as T
                                            }
                                        },
                                )

                            ConfirmarEmailScreen(
                                modifier = Modifier,
                                navController = navController,
                                idDoadora = idDoadora!!,
                                confirmarEmailScreenViewModel = confirmarEmailScreenViewModel,
                            )
                        }

                        composable(route = "dashboard") {
                            val dashboardScreenViewModel: DashboardScreenViewModel =
                                viewModel(
                                    factory =
                                        object : ViewModelProvider.Factory {
                                            @Suppress("UNCHECKED_CAST")
                                            override fun <T : ViewModel> create(
                                                modelClass: Class<T>,
                                            ): T {
                                                return DashboardScreenViewModel(
                                                    context = context.applicationContext,
                                                )
                                                    as T
                                            }
                                        },
                                )

                            val context = LocalContext.current

                            val appSettingsDao =
                                remember(context) {
                                    MaternaDatabaseProvider.getDatabase(context).appSettingsDao()
                                }

                            val appSettings by
                                remember(appSettingsDao) {
                                        appSettingsDao.observeSettings()
                                    }
                                    .collectAsState(initial = null)

                            val idDoadora = appSettings?.idDoadora

                            LaunchedEffect(idDoadora) {
                                if (idDoadora != null && idDoadora > 0) {
                                    servicosCompartilhado.buscarLocalizacaoDoadora(idDoadora)

                                    dashboardScreenViewModel.buscarNomeDoadora(idDoadora)
                                } else {
                                    dashboardScreenViewModel.atualizarNome("")
                                }
                            }

                            DashboardScreen(
                                modifier = Modifier,
                                navController = navController,
                                dashboardScreenViewModel = dashboardScreenViewModel,
                            )
                        }

                        composable(route = "campanhas") {
                            val dashboardEntry =
                                remember(it) {
                                    navController.getBackStackEntry("dashboard")
                                }

                            val dashboardScreenViewModel: DashboardScreenViewModel =
                                viewModel(
                                    viewModelStoreOwner = dashboardEntry,
                                )

                            CampanhasScreen(
                                modifier = Modifier,
                                navController = navController,
                                dashboardScreenViewModel = dashboardScreenViewModel,
                            )
                        }

                        composable(
                            route = "campanha/{id}",
                            arguments =
                                listOf(
                                    navArgument("id") {
                                        type = NavType.IntType
                                    },
                                ),
                        ) { backStackEntry ->
                            val idCampanha = backStackEntry.arguments?.getInt("id")

                            val dashboardEntry =
                                remember(backStackEntry) {
                                    navController.getBackStackEntry("dashboard")
                                }

                            val dashboardScreenViewModel: DashboardScreenViewModel =
                                viewModel(
                                    viewModelStoreOwner = dashboardEntry,
                                )

                            val campanhas by
                                dashboardScreenViewModel.campanhas.observeAsState(emptyList())

                            val campanha = campanhas.find {
                                it.id == idCampanha
                            }

                            if (campanha != null) {
                                CampanhaScreen(
                                    modifier = Modifier,
                                    navController = navController,
                                    campanha = campanha,
                                )
                            }
                        }

                        composable(route = "jornada") {
                            val appSettings by
                                remember(settingsDao) {
                                        settingsDao.observeSettings()
                                    }
                                    .collectAsState(initial = null)

                            val idDoadora = appSettings?.idDoadora

                            val jornadaScreenViewModel: JornadaScreenViewModel =
                                viewModel(
                                    factory =
                                        object : ViewModelProvider.Factory {
                                            @Suppress("UNCHECKED_CAST")
                                            override fun <T : ViewModel> create(
                                                modelClass: Class<T>,
                                            ): T {
                                                return JornadaScreenViewModel(
                                                    context = context.applicationContext,
                                                )
                                                    as T
                                            }
                                        },
                                )

                            LaunchedEffect(idDoadora) {
                                if (idDoadora != null && idDoadora > 0) {
                                    val tag = "JORNADA_DEBUG"

                                    try {
                                        Log.d(tag, "Consultando vínculos da doadora: $idDoadora")

                                        val resposta =
                                            kotlinx.coroutines.withContext(
                                                kotlinx.coroutines.Dispatchers.IO,
                                            ) {
                                                com.example.materna.services.materna
                                                    .MaternaRetrofitFactory(
                                                        context.applicationContext,
                                                    )
                                                    .getVinculoEtapaDoadoraService()
                                                    .buscarVinculosDoadora(idDoadora)
                                                    .execute()
                                            }

                                        Log.d(tag, "HTTP: ${resposta.code()}")

                                        if (
                                            resposta.isSuccessful && resposta.body()?.status == true
                                        ) {
                                            val jornadaVinculada =
                                                resposta
                                                    .body()
                                                    ?.response
                                                    ?.jornadas
                                                    .orEmpty()
                                                    .firstOrNull { jornada ->
                                                        jornada.isAtivo == 1 &&
                                                            jornada.etapas.any { etapa ->
                                                                etapa.vinculos.any { vinculo ->
                                                                    vinculo.encerrado == 0 &&
                                                                        vinculo.status in
                                                                            listOf(
                                                                                "em_andamento",
                                                                                "concluida",
                                                                            )
                                                                }
                                                            }
                                                    }

                                            if (jornadaVinculada != null) {
                                                val idInstituicao = jornadaVinculada.instituicao.id

                                                Log.d(
                                                    tag,
                                                    "Recuperando instituição: $idInstituicao",
                                                )

                                                navController.navigate(
                                                    "jornada/$idInstituicao",
                                                ) {
                                                    popUpTo("jornada") {
                                                        inclusive = true
                                                    }

                                                    launchSingleTop = true
                                                }
                                            } else {
                                                jornadaScreenViewModel.exibirSemBanco()
                                            }
                                        } else {
                                            val erroApi = resposta.errorBody()?.string().orEmpty()

                                            Log.e(tag, "Resposta da API: $erroApi")

                                            if (
                                                resposta.code() == 404 &&
                                                    erroApi.contains("Não foram encontrados dados")
                                            ) {
                                                jornadaScreenViewModel.exibirSemBanco()
                                            } else {
                                                jornadaScreenViewModel.exibirErro(
                                                    "Não foi possível consultar sua jornada.",
                                                )
                                            }
                                        }
                                    } catch (e: kotlinx.coroutines.CancellationException) {
                                        throw e
                                    } catch (e: Exception) {
                                        Log.e(tag, "Erro ao recuperar jornada", e)

                                        jornadaScreenViewModel.exibirErro(
                                            "Não foi possível recuperar sua jornada.",
                                        )
                                    }
                                }
                            }

                            JornadaScreen(
                                modifier = Modifier,
                                navController = navController,
                                jornadaScreenViewModel = jornadaScreenViewModel,
                            )
                        }

                        composable(
                            route = "jornada/{id_instituicao}",
                            arguments =
                                listOf(
                                    navArgument("id_instituicao") {
                                        type = NavType.IntType
                                    },
                                ),
                        ) { backStackEntry ->
                            val idInstituicao =
                                backStackEntry.arguments?.getInt("id_instituicao")
                                    ?: return@composable

                            val appSettings by
                                remember(settingsDao) {
                                        settingsDao.observeSettings()
                                    }
                                    .collectAsState(initial = null)

                            val idDoadora = appSettings?.idDoadora

                            val jornadaScreenViewModel: JornadaScreenViewModel =
                                viewModel(
                                    factory =
                                        object : ViewModelProvider.Factory {
                                            @Suppress("UNCHECKED_CAST")
                                            override fun <T : ViewModel> create(
                                                modelClass: Class<T>,
                                            ): T {
                                                return JornadaScreenViewModel(
                                                    context = context.applicationContext,
                                                )
                                                    as T
                                            }
                                        },
                                )

                            val atualizarJornada by
                                backStackEntry.savedStateHandle
                                    .getLiveData("atualizar_jornada", false)
                                    .observeAsState(false)

                            LaunchedEffect(atualizarJornada) {
                                if (atualizarJornada) {
                                    backStackEntry.savedStateHandle.remove<Boolean>(
                                        "atualizar_jornada",
                                    )

                                    jornadaScreenViewModel.recarregarJornada()
                                }
                            }

                            LaunchedEffect(idInstituicao, idDoadora) {
                                if (idDoadora != null && idDoadora > 0) {
                                    jornadaScreenViewModel.carregarInstituicao(
                                        idInstituicao = idInstituicao,
                                        idDoadora = idDoadora,
                                    )
                                }
                            }

                            JornadaScreen(
                                modifier = Modifier,
                                navController = navController,
                                jornadaScreenViewModel = jornadaScreenViewModel,
                            )
                        }

                        composable(
                            route = "conheca_doacao/{id_instituicao}",
                            arguments =
                                listOf(
                                    navArgument("id_instituicao") {
                                        type = NavType.IntType
                                    },
                                ),
                        ) { backStackEntry ->
                            val idInstituicao =
                                backStackEntry.arguments?.getInt("id_instituicao")
                                    ?: return@composable

                            val jornadaEntry =
                                remember(backStackEntry) {
                                    navController.getBackStackEntry(
                                        "jornada/$idInstituicao",
                                    )
                                }

                            val jornadaScreenViewModel: JornadaScreenViewModel =
                                viewModel(
                                    viewModelStoreOwner = jornadaEntry,
                                )

                            var salvando by remember {
                                mutableStateOf(false)
                            }

                            var mensagemErro by remember {
                                mutableStateOf<String?>(null)
                            }

                            Column {
                                mensagemErro?.let { mensagem ->
                                    Text(
                                        text = mensagem,
                                        color = colorResource(R.color.main_pink),
                                        modifier = Modifier.padding(16.dp),
                                    )
                                }

                                ConhecaDoacaoScreen(
                                    onConcluir = {
                                        if (!salvando) {
                                            salvando = true
                                            mensagemErro = null

                                            jornadaScreenViewModel.concluirEtapaEstatica(
                                                idEtapa = "conheca_doacao",
                                                onSucesso = {
                                                    salvando = false
                                                    navController.popBackStack()
                                                },
                                                onErro = { erro ->
                                                    salvando = false
                                                    mensagemErro = erro
                                                },
                                            )
                                        }
                                    },
                                    onVoltar = {
                                        navController.popBackStack()
                                    },
                                )
                            }
                        }

                        composable(
                            route = "orientacoes/{id_jornada}",
                            arguments =
                                listOf(
                                    navArgument("id_jornada") {
                                        type = NavType.IntType
                                    },
                                ),
                        ) { backStackEntry ->
                            val idJornada = backStackEntry.arguments?.getInt("id_jornada")

                            val appSettings by
                                remember(settingsDao) {
                                        settingsDao.observeSettings()
                                    }
                                    .collectAsState(initial = null)

                            val idDoadora = appSettings?.idDoadora

                            val progressoDao =
                                remember(database) {
                                    database.jornadaLocalProgressDao()
                                }

                            OrientacoesScreen(
                                idDoadora = idDoadora,
                                idJornada = idJornada,
                                progressoDao = progressoDao,
                                onVoltar = {
                                    navController.popBackStack()
                                },
                                onConcluir = {
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("atualizar_jornada", true)

                                    navController.popBackStack()
                                },
                            )
                        }

                        composable(
                            route = "questionario/{id_jornada}",
                            arguments =
                                listOf(
                                    navArgument("id_jornada") {
                                        type = NavType.IntType
                                    },
                                ),
                        ) { backStackEntry ->
                            val idJornada = backStackEntry.arguments?.getInt("id_jornada")

                            val appSettings by
                                remember(settingsDao) {
                                        settingsDao.observeSettings()
                                    }
                                    .collectAsState(initial = null)

                            val idDoadora = appSettings?.idDoadora

                            val progressoDao =
                                remember(database) {
                                    database.jornadaLocalProgressDao()
                                }

                            QuestionarioScreen(
                                idDoadora = idDoadora,
                                idJornada = idJornada,
                                progressoDao = progressoDao,
                                onVoltar = {
                                    navController.popBackStack()
                                },
                                onConcluir = {
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("atualizar_jornada", true)

                                    navController.popBackStack()
                                },
                            )
                        }

                        composable(
                            route =
                                "agendamento/{id_instituicao}/{id_jornada}/{id_etapa}/{id_coleta}/{id_doadora}/{id_jornada_doacao}?nome_instituicao={nome_instituicao}",
                            arguments =
                                listOf(
                                    navArgument("id_instituicao") {
                                        type = NavType.IntType
                                    },
                                    navArgument("id_jornada") {
                                        type = NavType.IntType
                                    },
                                    navArgument("id_etapa") {
                                        type = NavType.IntType
                                    },
                                    navArgument("nome_instituicao") {
                                        type = NavType.StringType
                                        defaultValue = "Instituição"
                                    },
                                    navArgument("id_coleta") {
                                        type = NavType.IntType
                                    },
                                    navArgument("id_doadora") {
                                        type = NavType.IntType
                                    },
                                    navArgument("id_jornada_doacao") {
                                        type = NavType.IntType
                                    },
                                ),
                        ) { backStackEntry ->
                            val idInstituicao =
                                backStackEntry.arguments?.getInt("id_instituicao")
                                    ?: return@composable
                            val idJornada =
                                backStackEntry.arguments?.getInt("id_jornada") ?: return@composable
                            val idEtapa =
                                backStackEntry.arguments?.getInt("id_etapa") ?: return@composable
                            val nomeInstituicao =
                                backStackEntry.arguments
                                    ?.getString("nome_instituicao")
                                    .orEmpty()
                                    .ifBlank { "Instituição" }
                            val idColeta =
                                backStackEntry.arguments?.getInt("id_coleta") ?: return@composable
                            val idDoadora =
                                backStackEntry.arguments?.getInt("id_doadora") ?: return@composable
                            val idJornadaDoacao =
                                backStackEntry.arguments?.getInt("id_jornada_doacao")
                                    ?: return@composable

                            Log.d(
                                "AGENDAMENTO_NAV",
                                "Instituição: $idInstituicao | " +
                                    "Jornada: $idJornada | " +
                                    "Etapa: $idEtapa | " +
                                    "Coleta: $idColeta | " +
                                    "Doadora: $idDoadora | " +
                                    "Vínculo: $idJornadaDoacao",
                            )
                            AgendamentoScreen(
                                nomeInstituicao = nomeInstituicao,
                                idInstituicao = idInstituicao,
                                iconeInstituicao = painterResource(R.drawable.ic_hospital),
                                iconeHorario = Icons.Default.Home,
                                idJornada = idJornada,
                                idEtapa = idEtapa,
                                idColeta = idColeta,
                                idDoadora = idDoadora,
                                idJornadaDoacao = idJornadaDoacao,
                                onVoltar = {
                                    navController.popBackStack()
                                },
                            )
                        }

                        composable(route = "servicos") {
                            ServicosScreen(
                                modifier = Modifier,
                                navController = navController,
                                servicosScreenViewModel = servicosCompartilhado,
                                onSelecionarBanco = { idInstituicao ->
                                    navController.navigate("confirmar_banco/$idInstituicao") {
                                        launchSingleTop = true
                                    }
                                },
                            )
                        }

                        composable(
                            route = "confirmar_banco/{id_instituicao}",
                            arguments =
                                listOf(
                                    navArgument("id_instituicao") {
                                        type = NavType.IntType
                                    },
                                ),
                        ) { backStackEntry ->
                            val idInstituicao =
                                backStackEntry.arguments?.getInt("id_instituicao")
                                    ?: return@composable

                            val appSettings by
                                remember(settingsDao) {
                                        settingsDao.observeSettings()
                                    }
                                    .collectAsState(initial = null)

                            val idDoadora = appSettings?.idDoadora

                            val instituicoes by
                                servicosCompartilhado.instituicoes.observeAsState(emptyList())

                            val instituicao = instituicoes.firstOrNull {
                                it.id == idInstituicao
                            }

                            val confirmarBancoScreenViewModel: ConfirmarBancoScreenViewModel =
                                viewModel(
                                    factory =
                                        object : ViewModelProvider.Factory {
                                            @Suppress("UNCHECKED_CAST")
                                            override fun <T : ViewModel> create(
                                                modelClass: Class<T>,
                                            ): T {
                                                return ConfirmarBancoScreenViewModel(
                                                    context = context.applicationContext,
                                                )
                                                    as T
                                            }
                                        },
                                )

                            val estadoConfirmacao by
                                confirmarBancoScreenViewModel.estado.observeAsState(
                                    ConfirmarBancoEstado.INICIAL,
                                )

                            LaunchedEffect(estadoConfirmacao) {
                                when (estadoConfirmacao) {
                                    ConfirmarBancoEstado.SEM_VINCULO,
                                    ConfirmarBancoEstado.MESMO_BANCO -> {
                                        navController.navigate("jornada/$idInstituicao") {
                                            launchSingleTop = true
                                        }
                                    }

                                    else -> Unit
                                }
                            }

                            if (instituicao != null) {
                                ConfirmarBancoScreen(
                                    modifier = Modifier,
                                    instituicao = instituicao,
                                    idDoadora = idDoadora,
                                    confirmarBancoScreenViewModel = confirmarBancoScreenViewModel,
                                    onVoltar = {
                                        navController.popBackStack()
                                    },
                                    onConfirmar = {
                                        navController.navigate("jornada/$idInstituicao") {
                                            launchSingleTop = true
                                        }
                                    },
                                    onVoltarInicio = {
                                        navController.navigate("dashboard") {
                                            popUpTo("dashboard") {
                                                inclusive = false
                                            }
                                            launchSingleTop = true
                                        }
                                    },
                                )
                            }
                        }

                        composable(route = "perfil") {
                            val perfilScreenViewModel: PerfilScreenViewModel =
                                viewModel(
                                    factory =
                                        object : ViewModelProvider.Factory {
                                            @Suppress("UNCHECKED_CAST")
                                            override fun <T : ViewModel> create(
                                                modelClass: Class<T>,
                                            ): T {
                                                return PerfilScreenViewModel(
                                                    settingsDao = settingsDao,
                                                    context = context.applicationContext,
                                                )
                                                    as T
                                            }
                                        },
                                )

                            PerfilScreen(
                                modifier = Modifier,
                                navController = navController,
                                perfilScreenViewModel = perfilScreenViewModel,
                            )
                        }

                        composable(route = "editar_perfil") {
                            val editarPerfilScreenViewModel: EditarPerfilScreenViewModel =
                                viewModel(
                                    factory =
                                        object : ViewModelProvider.Factory {
                                            @Suppress("UNCHECKED_CAST")
                                            override fun <T : ViewModel> create(
                                                modelClass: Class<T>,
                                            ): T {
                                                return EditarPerfilScreenViewModel(
                                                    settingsDao = settingsDao,
                                                    context = context.applicationContext,
                                                )
                                                    as T
                                            }
                                        },
                                )

                            EditarPerfilScreen(
                                modifier = Modifier,
                                navController = navController,
                                editarPerfilScreenViewModel = editarPerfilScreenViewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}
