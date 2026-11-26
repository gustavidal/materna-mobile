package com.example.materna.screens.perfil

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.materna.R
import com.example.materna.components.PerfilIndicador
import com.example.materna.components.PerfilOpcao
import com.example.materna.session.SessionEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    perfilScreenViewModel: PerfilScreenViewModel = viewModel()
) {
    val doadora          by perfilScreenViewModel.doadora.observeAsState(null)
    val carregando       by perfilScreenViewModel.carregando.observeAsState(false)
    val mensagemErro     by perfilScreenViewModel.mensagemErro.observeAsState(null)

    val backStackEntry   by navController.currentBackStackEntryAsState()
    val perfilAtualizado  = backStackEntry?.savedStateHandle?.get<Boolean>("perfil_atualizado") == true

    LaunchedEffect(perfilAtualizado) {
        if (perfilAtualizado) {
            doadora?.id?.let { id ->
                perfilScreenViewModel.buscarDoadora(id)
            }
            backStackEntry?.savedStateHandle?.remove<Boolean>("perfil_atualizado")
        }
    }

    val context = LocalContext.current


    val selecionarFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            perfilScreenViewModel.atualizarFotoPerfil(
                context = context,
                uri = uri
            )
        }
    }

    val atualizandoFoto by perfilScreenViewModel.atualizandoFoto.observeAsState(false)

    val fotoPerfil by perfilScreenViewModel.fotoPerfil.observeAsState(null)

    val saindoDaConta by perfilScreenViewModel.saindoDaConta.observeAsState(false)

    val logoutRealizado by perfilScreenViewModel.logoutRealizado.observeAsState(false)

    val erroLogout by perfilScreenViewModel.erroLogout.observeAsState(null)

    var exibirDialogLogout by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(logoutRealizado) {
        if (logoutRealizado) {
            exibirDialogLogout = false
            SessionEvents.notificarLogout()
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    if (exibirDialogLogout) {
        AlertDialog(
            onDismissRequest = {
                if (!saindoDaConta) {
                    exibirDialogLogout = false
                    perfilScreenViewModel.limparErroLogout()
                }
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = colorResource(R.color.main_pink)
                                .copy(alpha = 0.12f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = null,
                        tint = colorResource(R.color.main_pink),
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Sair da conta?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_text_color),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Você poderá entrar novamente quando desejar. O progresso da sua jornada será mantido.",
                        fontSize = 14.sp,
                        color = colorResource(R.color.secundary_gray),
                        textAlign = TextAlign.Center
                    )

                    erroLogout?.let { erro ->
                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = erro,
                            color = colorResource(R.color.red),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            perfilScreenViewModel.sairDaConta()
                        },
                        enabled = !saindoDaConta &&
                                !atualizandoFoto &&
                                !logoutRealizado,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.main_pink),
                            contentColor = colorResource(R.color.white)
                        )
                    ) {
                        if (saindoDaConta) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = colorResource(R.color.main_pink),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Sair da conta",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            exibirDialogLogout = false
                            perfilScreenViewModel.limparErroLogout()
                        },
                        enabled = !saindoDaConta && !logoutRealizado,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Continuar conectada",
                            color = colorResource(R.color.secundary_gray)
                        )
                    }
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = !saindoDaConta && erroLogout == null,
                dismissOnClickOutside = false
            ),
            containerColor = colorResource(R.color.white),
            shape = RoundedCornerShape(24.dp)
        )
    }

    PullToRefreshBox(
        isRefreshing = carregando,
        onRefresh = {
            if (!carregando && !atualizandoFoto) {
                perfilScreenViewModel.carregarPerfil()
            }
        },
        state = pullToRefreshState,
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream)),
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = carregando,
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = colorResource(R.color.background_gray),
                            shape = CircleShape
                        )
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Voltar",
                        tint = colorResource(R.color.main_text_color)
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = "Perfil",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.main_text_color)
                    )

                    Text(
                        text = "Suas informações",
                        fontSize = 14.sp,
                        color = colorResource(R.color.secundary_gray)
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(
                        colorResource(R.color.main_pink)
                            .copy(alpha = 0.12f)
                    )
                    .clickable(enabled = !atualizandoFoto) {
                        selecionarFoto.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!fotoPerfil.isNullOrBlank()) {
                    AsyncImage(
                        model = fotoPerfil,
                        contentDescription = "Foto de perfil da doadora",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar da doadora",
                        modifier = Modifier.size(58.dp),
                        tint = colorResource(R.color.main_pink)
                    )
                }

                if (atualizandoFoto) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = colorResource(R.color.main_pink)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (carregando && doadora == null) {
                CircularProgressIndicator(
                    color = colorResource(R.color.main_pink),
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = doadora?.nome ?: "Nome não disponível",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_text_color)
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Doadora",
                fontSize = 14.sp,
                color = colorResource(R.color.secundary_gray)
            )

            if (mensagemErro != null) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = mensagemErro.orEmpty(),
                    fontSize = 13.sp,
                    color = colorResource(R.color.red)
                )
            }

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PerfilIndicador(
                    valor = doadora?.let {
                        "${it.etapasConcluidas}/${it.totalEtapas}"
                    } ?: "—",
                    descricao = "Etapas da jornada",
                    cor = colorResource(R.color.main_pink),
                    modifier = Modifier.weight(1f)
                )

                PerfilIndicador(
                    valor = doadora?.doacoesRealizadas?.toString() ?: "—",
                    descricao = "Doações realizadas",
                    cor = colorResource(R.color.green),
                    modifier = Modifier.weight(1f)
                )

                PerfilIndicador(
                    valor = doadora?.totalAgendamentos?.toString() ?: "—",
                    descricao = "Agendamentos",
                    cor = colorResource(R.color.main_text_color),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colorResource(R.color.white),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(vertical = 8.dp)
            ) {
                PerfilOpcao(
                    titulo = "Meus agendamentos",
                    simbolo = "▦",
                    onClick = {
                        // Aguardando rota de agendamentos.
                    }
                )

                PerfilOpcao(
                    titulo = "Editar informações",
                    simbolo = "✎",
                    onClick = {
                        navController.navigate("editar_perfil")
                    }
                )

                PerfilOpcao(
                    titulo = "Sobre o Materna",
                    simbolo = "ⓘ",
                    onClick = {
                        // Aguardando tela Sobre.
                    }
                )
            }

            Spacer(Modifier.height(28.dp))

            OutlinedButton(
                onClick = {
                    perfilScreenViewModel.limparErroLogout()
                    exibirDialogLogout = true
                },
                enabled = !atualizandoFoto && !saindoDaConta,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = CircleShape,
                border = BorderStroke(
                    width = 1.5.dp,
                    color = colorResource(R.color.main_pink)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorResource(R.color.main_pink)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.size(10.dp))

                Text(
                    text = "Sair da conta",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}