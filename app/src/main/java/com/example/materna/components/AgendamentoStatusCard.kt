package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import com.example.materna.models.responses.HorarioAgenda
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AgendamentoStatusCard(
    agendamento: HorarioAgenda,
    iconeStatus: ImageVector,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
    cancelamentoHabilitado: Boolean = false,
    isCancelando: Boolean = false
) {
    val pink = colorResource(R.color.main_pink)
    val texto = colorResource(R.color.main_text_color)
    val cinza = colorResource(R.color.secundary_gray)
    val white = colorResource(R.color.white)

    val confirmado = agendamento.idStatus == 2

    val corStatus = colorResource(
        if (confirmado) R.color.success_green
        else R.color.golden_august_dark
    )

    val fundoStatus = if (confirmado) {
        corStatus.copy(alpha = 0.12f)
    } else {
        colorResource(R.color.golden_august_background)
    }

    val titulo = if (confirmado) {
        "Agendamento confirmado"
    } else {
        "Aguardando confirmação"
    }

    val descricao = if (confirmado) {
        "Seu agendamento foi confirmado pela instituição."
    } else {
        "Sua solicitação foi enviada. Aguarde a confirmação da instituição."
    }

    val dataFormatada = runCatching {
        Instant.parse(agendamento.data)
            .atZone(ZoneId.of("America/Sao_Paulo"))
            .toLocalDate()
            .format(
                DateTimeFormatter.ofPattern(
                    "dd 'de' MMMM 'de' yyyy",
                    Locale("pt", "BR")
                )
            )
    }.getOrDefault("Data não disponível")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = white,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = colorResource(R.color.border),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(fundoStatus, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconeStatus,
                contentDescription = titulo,
                tint = corStatus,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = titulo,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = texto,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = descricao,
            fontSize = 14.sp,
            color = cinza,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            color = colorResource(R.color.border)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            InformacaoAgendamento(
                titulo = "Etapa",
                valor = agendamento.etapa
            )

            InformacaoAgendamento(
                titulo = "Data",
                valor = dataFormatada
            )

            InformacaoAgendamento(
                titulo = "Horário",
                valor = agendamento.hora.take(5)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedButton(
            onClick = onCancelar,
            enabled = cancelamentoHabilitado,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(30.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (cancelamentoHabilitado) {
                    pink
                } else {
                    pink.copy(alpha = 0.4f)
                }
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = pink,
                disabledContentColor = pink.copy(alpha = 0.4f)
            )
        ) {
            if (isCancelando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = corStatus,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Cancelar agendamento",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun InformacaoAgendamento(
    titulo: String,
    valor: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 12.sp,
            color = colorResource(R.color.secundary_gray)
        )

        Text(
            text = valor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.main_text_color)
        )
    }
}