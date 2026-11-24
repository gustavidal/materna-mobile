package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun DataAgendamentoCard(
    diaSemana: String,
    dia: String,
    mes: String,
    selecionado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    val pink = colorResource(R.color.main_pink)
    val white = colorResource(R.color.white)
    val texto = colorResource(R.color.main_text_color)
    val cinza = colorResource(R.color.secundary_gray)

    Column(
        modifier = modifier
            .width(64.dp)
            .height(86.dp)
            .clip(shape)
            .background(
                if (selecionado) pink else white
            )
            .border(
                width = 1.dp,
                color = if (selecionado) pink
                else colorResource(R.color.border),
                shape = shape
            )
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = diaSemana,
            fontSize = 12.sp,
            color = if (selecionado) white else cinza
        )

        Text(
            text = dia,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (selecionado) white else texto
        )

        Text(
            text = mes,
            fontSize = 12.sp,
            color = if (selecionado) white else cinza
        )
    }
}
