package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import com.example.materna.models.domain.JornadaEtapa
import com.example.materna.models.domain.JornadaStatus
import com.example.materna.models.responses.DocumentoDoadoraResponse
import com.example.materna.screens.jornada.DocumentoEstado

@Composable
fun JornadaTimeline(
    etapas: List<JornadaEtapa>,
    modifier: Modifier = Modifier,
    onEtapaClick: (JornadaEtapa) -> Unit = {},
    onDocumentoEnviado: (etapaId: String, url: String) -> Unit = { _, _ -> },
    onEtapaConcluida: (etapaId: String) -> Unit = {},
    onPularEtapa: (etapaId: String) -> Unit = {},
    documentosPorEtapa: Map<String, DocumentoDoadoraResponse> = emptyMap(),
    estadoDocumentos: DocumentoEstado = DocumentoEstado.INICIAL,
    documentosEmRegistro: Set<String> = emptySet(),
    erroDocumentos: String? = null,
    errosRegistroDocumento: Map<String, String> = emptyMap(),
    onTentarNovamenteDocumentos: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        etapas.forEachIndexed { index, etapa ->
            val concluida = etapa.status == JornadaStatus.CONCLUIDA
            val atual = etapa.status == JornadaStatus.ATUAL
            val bloqueada = etapa.status == JornadaStatus.BLOQUEADA

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .width(36.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (concluida || atual) colorResource(R.color.main_pink) else colorResource(R.color.disabled),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (concluida) "✓" else "${index + 1}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (bloqueada) colorResource(R.color.secundary_gray) else colorResource(R.color.white)
                        )
                    }

                    if (index < etapas.lastIndex) {
                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .weight(1f)
                                .background(if (concluida) colorResource(R.color.main_pink) else colorResource(R.color.disabled))
                        )
                    }
                }

                JornadaStepCard(
                    etapa = etapa,
                    modifier = Modifier.weight(1f),
                    documentoAtual = documentosPorEtapa[etapa.id],
                    estadoDocumentos = estadoDocumentos,
                    onPularEtapa = onPularEtapa,
                    registrandoDocumento = etapa.id in documentosEmRegistro,
                    erroDocumentos = erroDocumentos,
                    erroRegistroDocumento = errosRegistroDocumento[etapa.id],
                    onTentarNovamenteDocumentos = onTentarNovamenteDocumentos,
                    onClick = {
                        onEtapaClick(etapa)
                    },
                    onDocumentoEnviado = { etapaId, url ->
                        onDocumentoEnviado(etapaId, url)
                    },
                    onEtapaConcluida = { etapaId ->
                        onEtapaConcluida(etapaId)
                    }
                )
            }
        }
    }
}