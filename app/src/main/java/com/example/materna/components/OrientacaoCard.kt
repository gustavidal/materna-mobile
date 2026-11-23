package com.example.materna.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import com.example.materna.screens.jornada.Orientacao

@Composable
fun OrientacaoCard(
    orientacao: Orientacao,
    visualizado: Boolean,
    onVisualizar: () -> Unit
) {
    var expandido by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expandido = !expandido

                if (expandido) {
                    onVisualizar()
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.white)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = orientacao.titulo,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.main_text_color)
                    )

                    Text(
                        text = orientacao.descricao,
                        fontSize = 12.sp,
                        color = colorResource(R.color.secundary_gray)
                    )
                }

                Text(
                    text = if (visualizado) "✓" else "›",
                    color = colorResource(
                        if (visualizado) R.color.success_green
                        else R.color.main_text_color
                    )
                )
            }

            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = orientacao.conteudo,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = colorResource(R.color.main_text_color)
                )
            }
        }
    }
}