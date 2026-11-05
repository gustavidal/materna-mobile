package com.example.materna.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import com.example.materna.models.domain.JornadaEtapa
import com.example.materna.models.domain.JornadaStatus

@Composable
fun JornadaStepCard(
    etapa: JornadaEtapa,
    modifier: Modifier = Modifier
) {
    val atual = etapa.status == JornadaStatus.ATUAL
    val bloqueada = etapa.status == JornadaStatus.BLOQUEADA

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (atual) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = colorResource(R.color.main_pink),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (bloqueada) {
                colorResource(R.color.disabled)
            } else {
                colorResource(R.color.white)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (atual) {
                Text(
                    text = "AÇÃO NECESSÁRIA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_pink),
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = etapa.titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (bloqueada) colorResource(R.color.secundary_gray) else colorResource(R.color.main_text_color)
            )

            Text(
                text = etapa.descricao,
                fontSize = 13.sp,
                color = colorResource(R.color.secundary_gray),
                lineHeight = 19.sp
            )
        }
    }
}