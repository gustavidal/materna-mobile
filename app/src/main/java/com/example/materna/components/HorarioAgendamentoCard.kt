package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun HorarioAgendamentoCard(
    horario: String,
    disponivel: Boolean,
    selecionado: Boolean,
    icone: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    val pink = colorResource(R.color.main_pink)
    val white = colorResource(R.color.white)
    val texto = colorResource(R.color.main_text_color)
    val cinza = colorResource(R.color.secundary_gray)

    val corFundo = when {
        !disponivel -> colorResource(R.color.background_gray)
        selecionado -> pink
        else -> white
    }

    val corConteudo = when {
        !disponivel -> cinza
        selecionado -> white
        else -> texto
    }

    Column(
        modifier = modifier
            .height(90.dp)
            .clip(shape)
            .background(corFundo)
            .border(
                width = 1.dp,
                color = if (selecionado && disponivel)
                    pink
                else
                    colorResource(R.color.border),
                shape = shape
            )
            .clickable(enabled = disponivel) {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = corConteudo,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = horario,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = corConteudo
        )

        if (!disponivel) {
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Indisponível",
                fontSize = 10.sp,
                color = cinza
            )
        }
    }
}
