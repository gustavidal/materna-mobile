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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Exibida quando a instituição finalizou oficialmente a jornada.
 */
@Composable
fun JornadaFinalizada(
    nomeInstituicao: String,
    dataFinalizacao: Long?,
    onIniciarNovaJornada: () -> Unit,
    onVoltarInicio: () -> Unit
) {
    val dataFormatada = dataFinalizacao?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(it))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.white)
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
                    .size(80.dp)
                    .background(
                        color = colorResource(R.color.main_pink).copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = colorResource(R.color.main_pink),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Jornada finalizada!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.main_text_color)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Obrigada por doar! Sua doação ajuda a alimentar e proteger " +
                        "bebês que mais precisam de leite materno.",
                fontSize = 13.5.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            if (nomeInstituicao.isNotBlank() || dataFormatada != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = listOfNotNull(
                        nomeInstituicao.takeIf { it.isNotBlank() },
                        dataFormatada?.let { "Concluída em $it" }
                    ).joinToString("\n"),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.secundary_gray)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onIniciarNovaJornada,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.main_pink)
                )
            ) {
                Text(
                    text = "Iniciar nova jornada",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            TextButton(
                onClick = onVoltarInicio,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Voltar ao início",
                    color = colorResource(R.color.secundary_gray)
                )
            }
        }
    }
}
