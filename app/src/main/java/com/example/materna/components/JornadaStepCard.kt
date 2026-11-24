package com.example.materna.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.materna.models.domain.JornadaEtapa
import com.example.materna.models.domain.JornadaStatus

@Composable
fun JornadaStepCard(
    etapa: JornadaEtapa,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDocumentoEnviado: (etapaId: String, url: String) -> Unit = { _, _ -> }
) {
    val atual = etapa.status == JornadaStatus.ATUAL
//    val bloqueada = etapa.status == JornadaStatus.BLOQUEADA

    // TODO: Restaurar após os testes de agendamento
    val bloqueada =
        etapa.status == JornadaStatus.BLOQUEADA &&
                etapa.isAgendavel != 1

    LaunchedEffect(etapa) {
        Log.d(
            "JORNADA_AGENDAMENTO",
            "ID: ${etapa.id} | " +
                    "Título: ${etapa.titulo} | " +
                    "Status: ${etapa.status} | " +
                    "Agendável: ${etapa.isAgendavel}"
        )
    }

    var expandido by remember(etapa.id) {
        mutableStateOf(false)
    }

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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (atual && etapa.id.startsWith("instituicao_")) {
                            Modifier.clickable {
                                expandido = !expandido
                            }
                        } else if (atual) {
                            Modifier.clickable {
                                onClick()
                            }
                        } else {
                            Modifier
                        }
                    ),
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
                    color = if (bloqueada) {
                        colorResource(R.color.secundary_gray)
                    } else {
                        colorResource(R.color.main_text_color)
                    }
                )

                if (!atual || !etapa.id.startsWith("instituicao_")) {
                    Text(
                        text = etapa.descricao,
                        fontSize = 13.sp,
                        color = colorResource(R.color.secundary_gray),
                        lineHeight = 19.sp
                    )
                } else {
                    AnimatedVisibility(visible = expandido) {
                        Text(
                            text = etapa.descricao,
                            fontSize = 13.sp,
                            color = colorResource(R.color.secundary_gray),
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            if (
//                atual &&
                etapa.id.startsWith("instituicao_") &&
                etapa.isAgendavel == 1
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.main_pink),
                        contentColor = colorResource(R.color.white)
                    )
                ) {
                    Text(
                        text = "Agendar atendimento",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Área fixa: independente da descrição
            if (
                atual &&
                etapa.id.startsWith("instituicao_") &&
                etapa.isSolicitaArquivo == 0
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                DocumentoEtapa(
                    onEnviarDocumento = { url ->
                        onDocumentoEnviado(etapa.id, url)
                    }
                )
            }

            if (atual && etapa.id.startsWith("instituicao_")) {
                val funcionalidades = buildList {
                    if (etapa.isPrecisaAprovacao == 1) {
                        add("Aprovação necessária")
                    }

                    if (etapa.isRepetivel == 1) {
                        add("Etapa repetível")
                    }

                    if (etapa.isObrigatorio == 1) {
                        add("Etapa obrigatória")
                    }
                }

                if (funcionalidades.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    funcionalidades.forEach { funcionalidade ->
                        Text(
                            text = "• $funcionalidade",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(R.color.main_pink)
                        )
                    }
                }
            }
        }
    }
}
