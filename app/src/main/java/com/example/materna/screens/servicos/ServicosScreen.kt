package com.example.materna.screens.servicos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.CadastroField
import com.example.materna.components.MapaBancosLeite
import com.example.materna.components.ServicosFeedback
import com.example.materna.models.responses.InstituicaoProxima

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream))
            .verticalScroll(rememberScrollState())
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp,
                bottom = 32.dp
            ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = colorResource(R.color.main_text_color)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Encontrar serviço",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_text_color)
                )

                Text(
                    text = "Bancos de leite próximos",
                    fontSize = 13.sp,
                    color = colorResource(R.color.secundary_gray)
                )
            }
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
                enabled = cep.filter { it.isDigit() }.length == 8 && estado != ServicosEstado.CARREGANDO,
                modifier = Modifier.height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.main_pink),
                    contentColor = colorResource(R.color.disabled),
                    disabledContainerColor = colorResource(R.color.disabled),
                    disabledContentColor = colorResource(R.color.secundary_gray)
                )
            ) {
                Text(
                    text = "Buscar",
                    fontSize = 13.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            MapaBancosLeite(
                instituicoes = if (estado == ServicosEstado.SUCESSO) {
                    instituicoes
                } else {
                    emptyList()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = "Bancos de leite próximos",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.main_text_color)
        )

        when (estado) {
            ServicosEstado.INICIAL -> {
                Text(
                    text = "Informe seu CEP para encontrar bancos de leite.",
                    fontSize = 14.sp,
                    color = colorResource(R.color.secundary_gray)
                )
            }

            ServicosEstado.CARREGANDO -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.main_pink)
                    )
                }
            }

            ServicosEstado.SUCESSO -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    instituicoes.forEach { instituicao ->
                        BancoLeiteCard(
                            instituicao = instituicao,
                            onSelecionar = {
                                onSelecionarBanco(instituicao.id)
                            }
                        )
                    }
                }
            }

            ServicosEstado.VAZIO -> {
                ServicosFeedback(
                    titulo = "Nenhum banco encontrado",
                    descricao = "Não encontramos bancos de leite próximos a esse CEP. Experimente buscar por outro endereço."
                )
            }

            ServicosEstado.ERRO -> {
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

@Composable
private fun BancoLeiteCard(
    instituicao: InstituicaoProxima,
    onSelecionar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFF0E5E5),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color(0xFFFFEFF1),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = colorResource(R.color.main_pink),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = instituicao.nome,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.main_text_color)
                )

                Text(
                    text = "${instituicao.logradouro}, " +
                            "${instituicao.numero} - ${instituicao.bairro}",
                    fontSize = 12.sp,
                    color = colorResource(R.color.secundary_gray)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${instituicao.distanciaKm} km",
                        fontSize = 12.sp,
                        color = colorResource(R.color.main_pink)
                    )

                    Text(
                        text = instituicao.tipoColeta,
                        fontSize = 11.sp,
                        color = Color(0xFF36865C)
                    )
                }
            }
        }

        Button(
            onClick = onSelecionar,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.main_pink)
            )
        ) {
            Text(
                text = "Selecionar este banco",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
