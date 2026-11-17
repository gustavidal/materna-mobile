package com.example.materna.screens.servicos

import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.BancoLeiteCard
import com.example.materna.components.CadastroField
import com.example.materna.components.MapaBancosLeite
import com.example.materna.components.ServicosFeedback
import com.example.materna.data.local.MaternaDatabaseProvider
import com.example.materna.models.responses.InstituicaoProxima
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicosScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    servicosScreenViewModel: ServicosScreenViewModel = viewModel(),
    onSelecionarBanco: (Int) -> Unit = {}
) {
    val cep          by servicosScreenViewModel.cep.observeAsState("")
    val estado       by servicosScreenViewModel.estado.observeAsState(ServicosEstado.INICIAL)
    val instituicoes by servicosScreenViewModel.instituicoes.observeAsState(emptyList())
    val mensagemErro by servicosScreenViewModel.mensagemErro.observeAsState("")
    val pontosRota   by servicosScreenViewModel.pontosRota.observeAsState(emptyList())

    val context = LocalContext.current

    val appSettingsDao = remember(context) {
        MaternaDatabaseProvider.getDatabase(context)
            .appSettingsDao()
    }

    val appSettings by remember(appSettingsDao) {
        appSettingsDao.observeSettings()
    }.collectAsState(initial = null)

    var instituicaoSelecionada by remember {
        mutableStateOf<InstituicaoProxima?>(null)
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val isDebug = (context.applicationInfo.flags and
            ApplicationInfo.FLAG_DEBUGGABLE) != 0

    val idDoadora = appSettings?.idDoadora
        ?: if (isDebug) 1 else null

    LaunchedEffect(idDoadora) {
        Log.d(
            "MaternaDoadora",
            "ID da doadora no Room: $idDoadora"
        )

        idDoadora?.let {
            servicosScreenViewModel.buscarLocalizacaoDoadora(it)
        }
    }

    val localizacaoDoadora by servicosScreenViewModel.localizacaoDoadora.observeAsState(null)

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 190.dp,
        sheetContainerColor = colorResource(R.color.background_cream),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
            ) {
                Text(
                    text = "Bancos de leite próximos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.main_text_color),
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 12.dp
                    )
                )

                AnimatedVisibility(
                    visible = instituicaoSelecionada != null
                ) {
                    TextButton(
                        onClick = {
                            instituicaoSelecionada = null
                            servicosScreenViewModel.cancelarRota()

                            coroutineScope.launch {
                                bottomSheetState.partialExpand()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = colorResource(R.color.golden_august_dark)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Cancelar rota",
                            color = colorResource(R.color.golden_august_dark),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (estado) {
                        ServicosEstado.INICIAL -> item {
                            Text(
                                text = "Informe seu CEP para encontrar bancos de leite.",
                                color = colorResource(R.color.secundary_gray)
                            )
                        }

                        ServicosEstado.CARREGANDO -> item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = colorResource(R.color.main_pink)
                                )
                            }
                        }


                        ServicosEstado.SUCESSO -> {
                            items(
                                items = instituicoes,
                                key = { instituicao -> instituicao.id }
                            ) { instituicao ->
                                BancoLeiteCard(
                                    instituicao = instituicao,
                                    selecionado = instituicaoSelecionada?.id == instituicao.id,
                                    onSelecionar = {
                                        onSelecionarBanco(instituicao.id)
                                    }
                                )
                            }
                        }


                        ServicosEstado.VAZIO -> item {
                            ServicosFeedback(
                                titulo = "Nenhum banco encontrado",
                                descricao = "Não encontramos bancos de leite próximos a esse CEP."
                            )
                        }

                        ServicosEstado.ERRO -> item {
                            ServicosFeedback(
                                titulo = "Ops! O leite derramou...",
                                descricao = mensagemErro,
                                onTentarNovamente = {
                                    servicosScreenViewModel.buscarInstituicoes()
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MapaBancosLeite(
                instituicoes = instituicoes,
                localizacaoDoadora = localizacaoDoadora,
                onInstituicaoClick = { instituicao ->
                    if (instituicaoSelecionada?.id == instituicao.id) {
                        instituicaoSelecionada = null
                        servicosScreenViewModel.cancelarRota()
                    } else {
                        instituicaoSelecionada = instituicao
                        servicosScreenViewModel.buscarRota(instituicao)

                        val index = instituicoes.indexOfFirst {
                            it.id == instituicao.id
                        }

                        if (index >= 0) {
                            coroutineScope.launch {
                                bottomSheetState.expand()
                                listState.animateScrollToItem(index)
                            }
                        }
                    }
                },
                pontosRota = pontosRota,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = colorResource(R.color.main_text_color)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Encontrar serviço",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.main_text_color)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CadastroField(
                        value = cep,
                        onValueChange = {
                            servicosScreenViewModel.onCepChanged(it)
                        },
                        placeholder = "00000-000",
                        modifier = Modifier.weight(1f),
                        icon = painterResource(R.drawable.ic_pin_location),
                        keyboardType = KeyboardType.Number
                    )

                    Button(
                        onClick = {
                            servicosScreenViewModel.buscarInstituicoes()
                        },
                        enabled = cep.filter { it.isDigit() }.length == 8 &&
                                estado != ServicosEstado.CARREGANDO,
                        modifier = Modifier.height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.main_pink)
                        )
                    ) {
                        Text("Buscar")
                    }
                }
            }
        }
    }
}