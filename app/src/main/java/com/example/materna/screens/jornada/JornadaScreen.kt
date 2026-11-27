package com.example.materna.screens.jornada

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.JornadaAguardandoFinalizacao
import com.example.materna.components.JornadaFinalizada
import com.example.materna.components.JornadaProgressCard
import com.example.materna.components.JornadaTimeline
import com.example.materna.models.domain.JornadaStatus
import androidx.compose.material3.Button
import androidx.compose.material3.Icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JornadaScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    jornadaScreenViewModel: JornadaScreenViewModel = viewModel()
) {
    val estado                 by jornadaScreenViewModel.estado.observeAsState(JornadaEstado.CARREGANDO)

    val progresso              by jornadaScreenViewModel.progresso.observeAsState(0)
    val etapas                 by jornadaScreenViewModel.etapas.observeAsState(emptyList())

    val documentosPorEtapa     by jornadaScreenViewModel.documentosPorEtapa.observeAsState(emptyMap())
    val estadoDocumentos       by jornadaScreenViewModel.estadoDocumentos.observeAsState(DocumentoEstado.INICIAL)
    val documentosEmRegistro   by jornadaScreenViewModel.documentosEmRegistro.observeAsState(emptySet())
    val erroDocumentos         by jornadaScreenViewModel.erroDocumentos.observeAsState()
    val errosRegistroDocumento by jornadaScreenViewModel.errosRegistroDocumento.observeAsState(emptyMap())

    val mensagemErro           by jornadaScreenViewModel.mensagemErro.observeAsState("")

    val nomeInstituicao        by jornadaScreenViewModel.nomeInstituicao.observeAsState("")
    val idColeta               by jornadaScreenViewModel.idColeta.observeAsState()
    val vinculosPorEtapa       by jornadaScreenViewModel.vinculosPorEtapa.observeAsState(emptyMap())

    val cancelandoJornada      by jornadaScreenViewModel.cancelandoJornada.observeAsState(false)
    val erroCancelamento       by jornadaScreenViewModel.erroCancelamento.observeAsState()
    val jornadaCancelada       by jornadaScreenViewModel.jornadaCancelada.observeAsState(false)
    val dataFinalizacao        by jornadaScreenViewModel.dataFinalizacao.observeAsState(null)

    var mostrarDialogoCancelamento by remember {
        mutableStateOf(false)
    }

    val primeiraEtapaConcluida = etapas
        .firstOrNull { it.id == "conheca_doacao" }
        ?.status == JornadaStatus.CONCLUIDA

    var mostrarAvisoSaida by remember {
        mutableStateOf(false)
    }

    BackHandler(
        enabled = estado == JornadaEstado.COM_BANCO && !primeiraEtapaConcluida
    ) {
        mostrarAvisoSaida = true
    }

    val pullToRefreshState = rememberPullToRefreshState()

    var atualizandoJornada by remember {
        mutableStateOf(false)
    }

    val carregandoJornada =
        estado == JornadaEstado.CARREGANDO

    LaunchedEffect(estado) {
        if (!carregandoJornada) {
            atualizandoJornada = false
        }
    }

    PullToRefreshBox(
        isRefreshing = atualizandoJornada && carregandoJornada,
        state = pullToRefreshState,
        onRefresh = {
            if (
                !carregandoJornada &&
                !cancelandoJornada &&
                documentosEmRegistro.isEmpty() &&
                estadoDocumentos != DocumentoEstado.CARREGANDO
            ) {
                atualizandoJornada = true
                jornadaScreenViewModel.recarregarJornada()
            }
        },
        modifier = Modifier.fillMaxSize(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = atualizandoJornada && carregandoJornada,
                state = pullToRefreshState,
                containerColor = colorResource(R.color.white),
                color = colorResource(R.color.main_pink)
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colorResource(R.color.background_cream))
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 32.dp,
                    bottom = 32.dp
                ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Minha jornada",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_text_color)
                )

                Text(
                    text = "Acompanhe cada etapa",
                    fontSize = 14.sp,
                    color = colorResource(R.color.secundary_gray)
                )
            }

            when (estado) {
                JornadaEstado.CARREGANDO -> {
                    if (!atualizandoJornada) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(42.dp),
                                    color = colorResource(R.color.main_pink),
                                    strokeWidth = 4.dp
                                )

                                Text(
                                    text = "Carregando sua jornada...",
                                    fontSize = 14.sp,
                                    color = colorResource(R.color.secundary_gray)
                                )
                            }
                        }
                    }
                }

                JornadaEstado.SEM_BANCO -> Unit

                JornadaEstado.FINALIZADA -> {
                    JornadaFinalizada(
                        nomeInstituicao = nomeInstituicao,
                        dataFinalizacao = dataFinalizacao,
                        onIniciarNovaJornada = {
                            jornadaScreenViewModel.iniciarNovaJornada {
                                navController.navigate("servicos") {
                                    launchSingleTop = true
                                }
                            }
                        },
                        onVoltarInicio = {
                            navController.navigate("dashboard") {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                JornadaEstado.COM_BANCO,
                JornadaEstado.AGUARDANDO_FINALIZACAO -> {
                    JornadaProgressCard(
                        progresso = progresso
                    )

                    if (estado == JornadaEstado.AGUARDANDO_FINALIZACAO) {
                        JornadaAguardandoFinalizacao(
                            nomeInstituicao = nomeInstituicao
                        )
                    }

                    JornadaTimeline(
                        etapas = etapas,
                        documentosPorEtapa = documentosPorEtapa,
                        estadoDocumentos = estadoDocumentos,
                        documentosEmRegistro = documentosEmRegistro,
                        erroDocumentos = erroDocumentos,
                        errosRegistroDocumento = errosRegistroDocumento,
                        onPularEtapa = { etapaId ->
                            jornadaScreenViewModel.pularEtapaOpcional(etapaId)
                        },
                        onTentarNovamenteDocumentos = {
                            jornadaScreenViewModel.buscarDocumentosDoadora()
                        },
                        onEtapaClick = { etapa ->
                            if (etapa.status == JornadaStatus.ATUAL) {
                                val idInstituicao = navController
                                    .currentBackStackEntry
                                    ?.arguments
                                    ?.getInt("id_instituicao")

                                val idJornada = jornadaScreenViewModel.idJornadaSelecionada

                                when (etapa.id) {
                                    "conheca_doacao" -> {
                                        if (idInstituicao != null && idInstituicao > 0) {
                                            navController.navigate(
                                                "conheca_doacao/$idInstituicao"
                                            )
                                        }
                                    }

                                    "orientacoes" -> {
                                        if (idJornada != null && idJornada > 0) {
                                            navController.navigate(
                                                "orientacoes/$idJornada"
                                            )
                                        }
                                    }

                                    "questionario" -> {
                                        if (idJornada != null && idJornada > 0) {
                                            navController.navigate(
                                                "questionario/$idJornada"
                                            )
                                        }
                                    }

                                    else -> {
                                        if (
                                            etapa.id.startsWith("instituicao_") &&
                                            etapa.isAgendavel == 1 &&
                                            idInstituicao != null &&
                                            idInstituicao > 0 &&
                                            idJornada != null &&
                                            idJornada > 0
                                        ) {
                                            val idEtapa = etapa.id
                                                .removePrefix("instituicao_")
                                                .toIntOrNull()

                                            val coleta = idColeta
                                            val idDoadora = jornadaScreenViewModel.idDoadoraSelecionada
                                            val idJornadaDoacao = vinculosPorEtapa[etapa.id]

                                            if (
                                                idEtapa != null &&
                                                idEtapa > 0 &&
                                                coleta != null &&
                                                coleta in 1..3 &&
                                                idDoadora != null &&
                                                idDoadora > 0 &&
                                                idJornadaDoacao != null &&
                                                idJornadaDoacao > 0
                                            ) {
                                                navController.navigate(
                                                    "agendamento/$idInstituicao/$idJornada/$idEtapa/" +
                                                            "$coleta/$idDoadora/$idJornadaDoacao" +
                                                            "?nome_instituicao=${Uri.encode(nomeInstituicao)}"
                                                ) {
                                                    launchSingleTop = true
                                                }
                                            } else {
                                                android.util.Log.e(
                                                    "AGENDAMENTO",
                                                    "Dados inválidos: " +
                                                            "Etapa=$idEtapa | " +
                                                            "Coleta=$coleta | " +
                                                            "Doadora=$idDoadora | " +
                                                            "Vínculo=$idJornadaDoacao"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        onDocumentoEnviado = { etapaId, url ->
                            jornadaScreenViewModel.registrarDocumentoEnviado(
                                etapaId = etapaId,
                                url = url
                            )
                        },
                        onEtapaConcluida = { etapaId ->
                            jornadaScreenViewModel.concluirEtapaSimples(etapaId)
                        }
                    )

                    // Após concluir todas as etapas (100%) não faz sentido cancelar a jornada
                    if (estado == JornadaEstado.COM_BANCO) {
                        TextButton(
                            onClick = {
                                mostrarDialogoCancelamento = true
                            },
                            enabled = !cancelandoJornada,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Cancelar jornada",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.main_pink)
                            )
                        }
                    }
                }

                JornadaEstado.ERRO -> Unit
            }
        }
    }

    if (mostrarDialogoCancelamento && !jornadaCancelada) {
        AlertDialog(
            onDismissRequest = {
                if (!cancelandoJornada) {
                    mostrarDialogoCancelamento = false
                }
            },
            title = {
                Text(
                    text = "Cancelar jornada?",
                    color = colorResource(R.color.main_text_color),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Tem certeza de que deseja cancelar sua jornada? Você poderá perder o progresso realizado.",
                        color = colorResource(R.color.secundary_gray)
                    )

                    if (cancelandoJornada) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = colorResource(R.color.main_pink),
                                strokeWidth = 2.dp
                            )

                            Text(
                                text = "Cancelando jornada...",
                                fontSize = 14.sp,
                                color = colorResource(R.color.secundary_gray)
                            )
                        }
                    }

                    erroCancelamento?.let { erro ->
                        Text(
                            text = erro,
                            fontSize = 13.sp,
                            color = androidx.compose.ui.graphics.Color.Red
                        )
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            mostrarDialogoCancelamento = false
                        },
                        enabled = !cancelandoJornada,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Continuar jornada",
                            color = colorResource(R.color.main_pink),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    TextButton(
                        onClick = {
                            jornadaScreenViewModel.cancelarJornada()
                        },
                        enabled = !cancelandoJornada,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Sim, cancelar",
                            color = colorResource(R.color.main_text_color),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = colorResource(R.color.white)
        )
    }

    if (jornadaCancelada) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "Jornada cancelada",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_text_color)
                )
            },
            text = {
                Text(
                    text = "Sua jornada foi cancelada com sucesso. " +
                            "Você poderá selecionar outro banco de leite " +
                            "quando desejar.",
                    fontSize = 14.sp,
                    color = colorResource(R.color.secundary_gray)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCancelamento = false

                        jornadaScreenViewModel.confirmarCancelamentoJornada()

                        navController.navigate("jornada") {
                            popUpTo("jornada/{id_instituicao}") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Entendido",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.main_pink)
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = colorResource(R.color.white)
        )
    }

    if (mostrarAvisoSaida) {
        AlertDialog(
            onDismissRequest = {
                mostrarAvisoSaida = false
            },
            title = {
                Text(
                    text = "Deseja sair da jornada?",
                    color = colorResource(R.color.main_text_color)
                )
            },
            text = {
                Text(
                    text = "Você ainda não concluiu a primeira etapa. " +
                            "Se sair agora, sua jornada não estará vinculada " +
                            "à instituição selecionada."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        navController.navigate("servicos")
                    }
                ) {
                    Text(
                        text = "Sair mesmo assim",
                        color = colorResource(R.color.main_pink)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {

                    }
                ) {
                    Text(
                        text = "Continuar jornada",
                        color = colorResource(R.color.main_text_color)
                    )
                }
            }
        )
    }

    val voltarParaServicos: () -> Unit = {
        if (!navController.popBackStack("servicos", false)) {
            navController.navigate("servicos") {
                popUpTo("jornada/{id_instituicao}") {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    if (estado == JornadaEstado.SEM_BANCO) {

        val sairDaJornada: () -> Unit = {
            if (!navController.popBackStack()) {
                navController.navigate("dashboard") {
                    launchSingleTop = true
                }
            }
        }

        AlertDialog(
            onDismissRequest = sairDaJornada,

            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = colorResource(R.color.pink_beige),
                            shape = RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_hospital),
                        contentDescription = null,
                        tint = colorResource(R.color.main_pink),
                        modifier = Modifier.size(32.dp)
                    )
                }
            },

            title = {
                Text(
                    text = "Você ainda não iniciou sua jornada",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.main_text_color),
                    modifier = Modifier.fillMaxWidth()
                )
            },

            text = {
                Text(
                    text = "Selecione um banco de leite para iniciar " +
                            "sua jornada de doação de leite materno.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.secundary_gray),
                    modifier = Modifier.fillMaxWidth()
                )
            },

            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = voltarParaServicos,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.main_pink),
                            contentColor = colorResource(R.color.white)
                        )
                    ) {
                        Text(
                            text = "Selecionar banco de leite",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    TextButton(
                        onClick = sairDaJornada,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Agora não",
                            color = colorResource(R.color.secundary_gray)
                        )
                    }
                }
            },

            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            ),

            shape = RoundedCornerShape(24.dp),
            containerColor = colorResource(R.color.white)
        )
    }

    if (estado == JornadaEstado.ERRO) {
        AlertDialog(
            onDismissRequest = voltarParaServicos,
            title = {
                Text(
                    text = "Ops! Algo deu errado",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_text_color)
                )
            },
            text = {
                Text(
                    text = mensagemErro.ifBlank {
                        "Não foi possível carregar sua jornada. Verifique sua conexão e tente novamente."
                    },
                    color = colorResource(R.color.secundary_gray)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        jornadaScreenViewModel.recarregarJornada()
                    }
                ) {
                    Text(
                        text = "Tentar novamente",
                        color = colorResource(R.color.main_pink),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = voltarParaServicos
                ) {
                    Text(
                        text = "Voltar",
                        color = colorResource(R.color.main_text_color)
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = colorResource(R.color.white)
        )
    }
}