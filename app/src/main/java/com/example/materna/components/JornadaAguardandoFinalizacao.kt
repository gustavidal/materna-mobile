package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

/**
 * Exibida quando a doadora concluiu todas as etapas (100%) e a jornada
 * ainda aguarda a finalização oficial pela instituição.
 */
@Composable
fun JornadaAguardandoFinalizacao(
    nomeInstituicao: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.golden_august_background)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = colorResource(R.color.golden_august).copy(alpha = 0.18f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = colorResource(R.color.golden_august_dark),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Você concluiu todas as etapas!",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.main_text_color)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (nomeInstituicao.isNotBlank()) {
                    "Agora é com o $nomeInstituicao: a instituição vai finalizar " +
                            "oficialmente a sua jornada. Puxe a tela para baixo " +
                            "para atualizar."
                } else {
                    "Agora é com a instituição: ela vai finalizar oficialmente " +
                            "a sua jornada. Puxe a tela para baixo para atualizar."
                },
                fontSize = 13.5.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.golden_august_dark)
            )
        }
    }
}
