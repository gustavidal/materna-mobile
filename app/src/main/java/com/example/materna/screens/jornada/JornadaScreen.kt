package com.example.materna.screens.jornada

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.JornadaProgressCard
import com.example.materna.components.JornadaSemBanco
import com.example.materna.components.JornadaTimeline

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
                    etapas = etapas
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

            JornadaEstado.ERRO -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cloud_off),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp),
                        tint = colorResource(R.color.main_text_color)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Ops! O leite derramou...",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.main_text_color)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = mensagemErro,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.secundary_gray)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            jornadaScreenViewModel.recarregarJornada()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.main_pink)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Tentar novamente",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = colorResource(R.color.white)
                        )
                    }
                }
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
}