package com.example.materna.screens.confirmarbanco

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import com.example.materna.models.responses.InstituicaoProxima
import java.util.Locale

@Composable
fun ConfirmarBancoScreen(
    instituicao: InstituicaoProxima,
    idDoadora: Int?,
    onVoltar: () -> Unit,
    onConfirmar: () -> Unit,
    onVoltarInicio: () -> Unit,
    confirmarBancoScreenViewModel: ConfirmarBancoScreenViewModel,
    modifier: Modifier = Modifier
) {
    val estado       by confirmarBancoScreenViewModel.estado.observeAsState(ConfirmarBancoEstado.INICIAL)

    val mensagemErro by confirmarBancoScreenViewModel.mensagemErro.observeAsState("")

    LaunchedEffect(estado) {
        when (estado) {
            ConfirmarBancoEstado.SEM_VINCULO,
            ConfirmarBancoEstado.MESMO_BANCO -> {
                onConfirmar()
            }

            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream))
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 36.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = onVoltar,
                    modifier = Modifier
                        .size(44.dp)
                        .background(colorResource(R.color.background_gray), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = colorResource(R.color.main_text_color),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Confirmar Banco de Leite",
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.main_text_color)
                    )

                    Text(
                        text = "Confirme seu ponto de referência para coleta e suporte durante a doação.",
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = colorResource(R.color.main_text_color)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        colorResource(R.color.white),
                        RoundedCornerShape(30.dp)
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                colorResource(R.color.pink_beige),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_hospital),
                            contentDescription = null,
                            tint = colorResource(R.color.main_pink),
                            modifier = Modifier.size(27.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(
                                        color = colorResource(R.color.success_green),
                                        shape = CircleShape
                                    )
                            )

                            Text(
                                text = "Ponto de referência selecionado",
                                fontSize = 11.sp,
                                color = colorResource(R.color.success_green)
                            )
                        }

                        Text(
                            text = instituicao.nome,
                            fontSize = 18.sp,
                            lineHeight = 23.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.main_text_color)
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = colorResource(R.color.border)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = colorResource(R.color.main_pink),
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = "${instituicao.logradouro}, " +
                                "${instituicao.numero} • " +
                                String.format(
                                    Locale("pt", "BR"),
                                    "%.1f km de você",
                                    instituicao.distanciaKm
                                ),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = colorResource(R.color.main_text_color)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = colorResource(R.color.background_gray)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = colorResource(R.color.success_green),
                            modifier = Modifier.size(15.dp)
                        )

                        Text(
                            text = instituicao.tipoColeta,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(R.color.main_text_color)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        colorResource(R.color.background_gray),
                        RoundedCornerShape(28.dp)
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 22.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colorResource(R.color.main_pink),
                    modifier = Modifier.size(22.dp)
                )

                Text(
                    text = "A doação de leite humano é um gesto de solidariedade que contribui para a saúde e o desenvolvimento de bebês que precisam desse cuidado.",
                    fontSize = 13.sp,
                    lineHeight = 21.sp,
                    color = colorResource(R.color.main_text_color)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    bottom = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Button(
                onClick = {
                    if (idDoadora != null && idDoadora > 0) {
                        confirmarBancoScreenViewModel.verificarVinculo(
                            idDoadora = idDoadora,
                            idInstituicao = instituicao.id
                        )
                    }
                },
                enabled = estado != ConfirmarBancoEstado.CARREGANDO && idDoadora != null && idDoadora > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(50.dp),
                        ambientColor = colorResource(R.color.main_pink).copy(alpha = 0.18f),
                        spotColor = colorResource(R.color.main_pink).copy(alpha = 0.28f)
                    ),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.main_pink),
                    contentColor = colorResource(R.color.white)
                )
            ) {
                if (estado == ConfirmarBancoEstado.CARREGANDO) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.main_pink),
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Iniciar minha jornada de doação",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            TextButton(onClick = onVoltarInicio) {
                Text(
                    text = "Voltar para o início",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.main_pink)
                )
            }
        }
    }

    if (
        estado == ConfirmarBancoEstado.OUTRO_BANCO ||
        estado == ConfirmarBancoEstado.ERRO
    ) {
        val outroBanco = estado == ConfirmarBancoEstado.OUTRO_BANCO

        AlertDialog(
            onDismissRequest = {
                confirmarBancoScreenViewModel.limparAviso()
            },
            title = {
                Text(
                    text = if (outroBanco)
                        "Jornada já vinculada"
                    else
                        "Não foi possível continuar",
                    color = colorResource(R.color.main_text_color),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (outroBanco)
                        "Você já possui uma jornada ativa em outro banco de leite. " +
                                "Para iniciar uma nova jornada, será necessário " +
                                "encerrar o vínculo anterior."
                    else
                        mensagemErro.ifBlank {
                            "Não foi possível verificar seus vínculos. " +
                                    "Tente novamente."
                        },
                    color = colorResource(R.color.secundary_gray)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarBancoScreenViewModel.limparAviso()
                    }
                ) {
                    Text(
                        text = "Entendi",
                        color = colorResource(R.color.main_pink),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = colorResource(R.color.white)
        )
    }
}
