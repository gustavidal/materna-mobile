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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import com.example.materna.models.responses.InstituicaoProxima
import java.util.Locale

@Composable
fun ConfirmarBancoScreen(
    instituicao: InstituicaoProxima,
    onVoltar: () -> Unit,
    onConfirmar: () -> Unit,
    onVoltarInicio: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = onVoltar,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0xFFF1EAE7),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = colorResource(R.color.main_text_color)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Confirmar Banco de Leite",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.main_text_color)
                    )

                    Text(
                        text = "Confirme seu ponto de referência para coleta e suporte durante a doação.",
                        fontSize = 15.sp,
                        lineHeight = 23.sp,
                        color = colorResource(R.color.main_text_color)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(28.dp)
                    )
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                Color(0xFFFFEFF1),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = colorResource(R.color.main_pink),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Banco de leite selecionado",
                            fontSize = 12.sp,
                            color = Color(0xFF36865C)
                        )

                        Text(
                            text = instituicao.nome,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.main_text_color)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF0E5E5))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = colorResource(R.color.main_pink),
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = "${instituicao.logradouro}, ${instituicao.numero} - " +
                                "${instituicao.bairro} • " +
                                String.format(
                                    Locale("pt", "BR"),
                                    "%.1f km",
                                    instituicao.distanciaKm
                                ),
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = colorResource(R.color.main_text_color)
                    )
                }

                Surface(
                    color = Color(0xFFF3EFED),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = instituicao.tipoColeta,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        ),
                        fontSize = 12.sp,
                        color = colorResource(R.color.main_text_color)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF3F1EF),
                        RoundedCornerShape(26.dp)
                    )
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = colorResource(R.color.main_pink)
                )

                Text(
                    text = "A doação de leite humano é um gesto de solidariedade que contribui para a saúde e o desenvolvimento de bebês que precisam desse cuidado.",
                    fontSize = 14.sp,
                    lineHeight = 23.sp,
                    color = colorResource(R.color.main_text_color)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onConfirmar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.main_pink)
                )
            ) {
                Text(
                    text = "Iniciar minha jornada de doação",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }

            TextButton(onClick = onVoltarInicio) {
                Text(
                    text = "Voltar para o início",
                    color = colorResource(R.color.main_text_color)
                )
            }
        }
    }
}
