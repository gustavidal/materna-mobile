package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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

@Composable
fun BancoLeiteCard(
    instituicao: InstituicaoProxima,
    selecionado: Boolean = false,
    onSelecionar: () -> Unit
) {
    val corFundo = if (selecionado) {
        colorResource(R.color.golden_august_background)
    } else {
        colorResource(R.color.white)
    }

    val corBorda = if (selecionado) {
        colorResource(R.color.golden_august)
    } else {
        colorResource(R.color.border)
    }

    val corDestaque = if (selecionado) {
        colorResource(R.color.golden_august)
    } else {
        colorResource(R.color.main_pink)
    }

    val corFundoIcone = if (selecionado) {
        colorResource(R.color.golden_august_background)
    } else {
        colorResource(R.color.background_cream)
    }

    val corBotao = if (selecionado) {
        colorResource(R.color.golden_august_dark)
    } else {
        colorResource(R.color.main_pink)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = corFundo,
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = if (selecionado) 2.dp else 1.dp,
                color = corBorda,
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
                        color = corFundoIcone,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = corDestaque,
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
                        color = corDestaque
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
                containerColor = corBotao
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
