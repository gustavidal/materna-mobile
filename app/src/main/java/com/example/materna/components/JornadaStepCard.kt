package com.example.materna.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
fun JornadaStepCard(
    etapa: JornadaEtapa,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDocumentoEnviado: (etapaId: String, url: String) -> Unit = { _, _ -> },
    onEtapaConcluida: (etapaId: String) -> Unit = {},
    onPularEtapa: (etapaId: String) -> Unit = {},
    documentoAtual: DocumentoDoadoraResponse? = null,
    estadoDocumentos: DocumentoEstado = DocumentoEstado.INICIAL,
    registrandoDocumento: Boolean = false,
    erroDocumentos: String? = null,
    erroRegistroDocumento: String? = null,
    onTentarNovamenteDocumentos: () -> Unit = {}
) {
    val atual = etapa.status == JornadaStatus.ATUAL
    val bloqueada = etapa.status == JornadaStatus.BLOQUEADA

    val context = LocalContext.current
    val documentoInstituicao = etapa.documento?.trim()

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
                atual &&
                etapa.id.startsWith("instituicao_") &&
                !documentoInstituicao.isNullOrBlank()
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.pink_beige)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Documento da instituição",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.main_text_color)
                        )

                        Text(
                            text = "Disponível para consulta",
                            fontSize = 12.sp,
                            color = colorResource(R.color.secundary_gray)
                        )

                        Button(
                            onClick = {
                                try {
                                    val uri = Uri.parse(documentoInstituicao)

                                    if (uri.scheme !in listOf("https", "http") ||
                                        uri.host.isNullOrBlank()
                                    ) {
                                        Toast.makeText(
                                            context,
                                            "Documento indisponível.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            uri
                                        )

                                        context.startActivity(intent)
                                    }
                                } catch (e: ActivityNotFoundException) {
                                    Log.e(
                                        "JornadaDocumento",
                                        "Não foi possível abrir o documento."
                                    )

                                    Toast.makeText(
                                        context,
                                        "Não foi possível abrir o documento.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
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
                                text = "Visualizar documento",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (
                atual &&
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
                etapa.isSolicitaArquivo == 1
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                DocumentoEtapa(
                    documentoAtual = documentoAtual,
                    estadoDocumentos = estadoDocumentos,
                    registrandoDocumento = registrandoDocumento,
                    erroDocumentos = erroDocumentos,
                    erroRegistroDocumento = erroRegistroDocumento,
                    onTentarNovamenteDocumentos = onTentarNovamenteDocumentos,
                    onEnviarDocumento = { url ->
                        onDocumentoEnviado(etapa.id, url)
                    }
                )
            }

            // Etapa opcional
            if (
                atual &&
                etapa.id.startsWith("instituicao_") &&
                etapa.isObrigatorio == 0 &&
                etapa.isRepetivel == 0
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onPularEtapa(etapa.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(
                            width = 1.dp,
                            color = colorResource(R.color.main_pink),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.white),
                        contentColor = colorResource(R.color.main_pink)
                    )
                ) {
                    Text(
                        text = "Pular etapa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Etapa simples
            if (
                atual &&
                etapa.id.startsWith("instituicao_") &&
                etapa.isAgendavel == 0 &&
                etapa.isSolicitaArquivo == 0 &&
                etapa.isPrecisaAprovacao == 0 &&
                etapa.isRepetivel == 0 &&
                etapa.isObrigatorio == 1
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onEtapaConcluida(etapa.id)
                    },
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
                        text = "Concluir etapa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
