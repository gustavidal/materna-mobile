package com.example.materna.screens.jornada

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.JornadaProgressCard
import com.example.materna.components.JornadaSemBanco
import com.example.materna.components.JornadaTimeline
import com.example.materna.models.domain.JornadaStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JornadaScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    jornadaScreenViewModel: JornadaScreenViewModel = viewModel()
) {
    val estado       by jornadaScreenViewModel.estado.observeAsState(JornadaEstado.CARREGANDO)

    val progresso    by jornadaScreenViewModel.progresso.observeAsState(0)
    val etapas       by jornadaScreenViewModel.etapas.observeAsState(emptyList())

    val mensagemErro by jornadaScreenViewModel.mensagemErro.observeAsState("")

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



    PullToRefreshBox(
        isRefreshing = estado == JornadaEstado.CARREGANDO,
        onRefresh = {
            jornadaScreenViewModel.recarregarJornada()
        },
        modifier = Modifier.fillMaxSize()
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorResource(R.color.main_pink),
                            strokeWidth = 4.dp
                        )
                    }
                }

                JornadaEstado.SEM_BANCO -> {
                    JornadaSemBanco(
                        onSelecionarBanco = {
                            navController.navigate("servicos") {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                JornadaEstado.COM_BANCO -> {
                    JornadaProgressCard(
                        progresso = progresso
                    )

                    JornadaTimeline(
                        etapas = etapas,
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
                                }
                            }
                        },
                        onDocumentoEnviado = { etapaId, url ->
                            jornadaScreenViewModel.registrarDocumentoEnviado(
                                etapaId = etapaId,
                                url = url
                            )
                        }
                    )

                    TextButton(
                        onClick = {
                            mostrarDialogoCancelamento = true
                        },
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

                JornadaEstado.ERRO -> Unit
            }
        }
    }

    if (mostrarDialogoCancelamento) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoCancelamento = false
            },
            title = {
                Text(
                    text = "Cancelar jornada?",
                    color = colorResource(R.color.main_text_color)
                )
            },
            text = {
                Text(
                    text = "Tem certeza de que deseja cancelar sua jornada? Você poderá perder o progresso realizado.",
                    color = colorResource(R.color.secundary_gray)
                )
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
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
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
                            mostrarDialogoCancelamento = false

                            // TODO: Integrar cancelamento com a API.
                        },
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
            }
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

    if (estado == JornadaEstado.ERRO) {
        AlertDialog(
            onDismissRequest = {
                navController.popBackStack()
            },
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
                        "Não foi possível carregar sua jornada. " +
                                "Verifique sua conexão e tente novamente."
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
                    onClick = {
                        navController.popBackStack()
                    }
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