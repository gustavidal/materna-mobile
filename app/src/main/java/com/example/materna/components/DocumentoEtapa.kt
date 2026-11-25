package com.example.materna.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.materna.R
import com.example.materna.models.responses.DocumentoDoadoraResponse
import com.example.materna.screens.jornada.DocumentoEstado
import com.example.materna.services.cloudinary.DocumentoEtapaUpload
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun DocumentoEtapa(
    modifier: Modifier = Modifier,
    onEnviarDocumento: (String) -> Unit = {},
    documentoAtual: DocumentoDoadoraResponse? = null,
    estadoDocumentos: DocumentoEstado = DocumentoEstado.INICIAL,
    registrandoDocumento: Boolean = false,
    erroDocumentos: String? = null,
    erroRegistroDocumento: String? = null,
    onTentarNovamenteDocumentos: () -> Unit = {}
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val upload = remember { DocumentoEtapaUpload() }

    var enviando by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf<String?>(null) }
    var urlDocumento by remember { mutableStateOf<String?>(null) }

    var documentoSelecionado by remember {
        mutableStateOf<Uri?>(null)
    }

    val seletorDocumento = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null && !enviando && !registrandoDocumento) {
            documentoSelecionado = uri
            urlDocumento = null
            mensagem = null
        }
    }

    if (registrandoDocumento) {
        DocumentoCarregamento(
            mensagem = "Registrando documento...",
            modifier = modifier
        )
        return
    }

    when (estadoDocumentos) {
        DocumentoEstado.INICIAL,
        DocumentoEstado.CARREGANDO -> {
            DocumentoCarregamento(
                mensagem = "Consultando documentos...",
                modifier = modifier
            )
            return
        }

        DocumentoEstado.ERRO -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DocumentoStatusCard(
                    titulo = "Falha na consulta",
                    descricao = erroDocumentos
                        ?: "Não foi possível consultar os documentos.",
                    corTexto = R.color.error_red,
                    corFundo = R.color.pink_beige
                )

                OutlinedButton(
                    onClick = onTentarNovamenteDocumentos,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Tentar novamente",
                        color = colorResource(R.color.main_pink)
                    )
                }
            }
            return
        }

        DocumentoEstado.SUCESSO -> Unit
    }

    if (documentoAtual != null) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (documentoAtual.idStatusDocumento) {
                2 -> {
                    DocumentoStatusCard(
                        titulo = "✓ Documento aceito",
                        descricao = "Seu documento foi aprovado com sucesso.",
                        corTexto = R.color.success_green,
                        corFundo = R.color.success_background
                    )
                }

                else -> {
                    DocumentoStatusCard(
                        titulo = documentoAtual.status,
                        descricao = "Consulte a situação do seu documento.",
                        corTexto = R.color.golden_august_dark,
                        corFundo = R.color.golden_august_background
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    try {
                        val uri = Uri.parse(documentoAtual.documento)

                        if (
                            uri.scheme != "https" ||
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
                        Toast.makeText(
                            context,
                            "Não foi possível abrir o documento.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Visualizar documento",
                    color = colorResource(R.color.main_pink),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        erroRegistroDocumento?.let { erro ->
            DocumentoStatusCard(
                titulo = "Falha no registro",
                descricao = erro,
                corTexto = R.color.error_red,
                corFundo = R.color.pink_beige
            )
        }

        Button(
            onClick = {
                seletorDocumento.launch(
                    arrayOf(
                        "application/pdf",
                        "image/jpeg",
                        "image/png"
                    )
                )

            },
            enabled = !enviando && !registrandoDocumento,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.main_pink),
                contentColor = colorResource(R.color.white)
            )
        ) {
            Text(
                text = "＋ Adicionar documento",
                fontWeight = FontWeight.SemiBold
            )
        }

        documentoSelecionado?.let { uri ->
            val tipo = remember(uri) {
                context.contentResolver.getType(uri)
            }

            val nome = remember(uri) {
                var resultado = "Documento selecionado"

                context.contentResolver.query(
                    uri,
                    arrayOf(OpenableColumns.DISPLAY_NAME),
                    null,
                    null,
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        resultado = cursor.getString(0)
                            ?: resultado
                    }
                }

                resultado
            }

            Text(
                text = nome,
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(R.color.main_text_color)
            )

            if (tipo?.startsWith("image/") == true) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Prévia do documento",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "Arquivo selecionado",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedButton(
                onClick = {
                    if (!enviando && !registrandoDocumento) {

                        val urlExistente = urlDocumento

                        if (urlExistente != null) {
                            onEnviarDocumento(urlExistente)
                        } else {
                            enviando = true
                            mensagem = null

                            scope.launch {
                                try {
                                    val resultado = upload.enviar(context, uri)

                                    resultado.onSuccess { url ->
                                        urlDocumento = url
                                        onEnviarDocumento(url)
                                    }

                                    resultado.onFailure { erro ->
                                        if (erro is CancellationException) {
                                            throw erro
                                        }

                                        mensagem = erro.message
                                            ?: "Não foi possível enviar o documento."
                                    }

                                } catch (erro: CancellationException) {
                                    throw erro

                                } catch (erro: Exception) {
                                    mensagem = erro.message
                                        ?: "Erro inesperado no envio."

                                } finally {
                                    enviando = false
                                }
                            }
                        }
                    }
                },
                enabled = !enviando &&
                        !registrandoDocumento &&
                        estadoDocumentos == DocumentoEstado.SUCESSO,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (enviando) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = colorResource(R.color.main_pink),
                            strokeWidth = 2.dp
                        )

                        Text(
                            text = "Enviando arquivo...",
                            color = colorResource(R.color.main_pink)
                        )
                    }
                } else {
                    Text(
                        text = if (urlDocumento != null) {
                            "Tentar registrar novamente"
                        } else {
                            "Enviar documento"
                        },
                        color = colorResource(R.color.main_pink)
                    )
                }
            }

            mensagem?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.error_red)
                )
            }
        }
    }
}

@Composable
private fun DocumentoStatusCard(
    titulo: String,
    descricao: String,
    corTexto: Int,
    corFundo: Int,
    motivoRecusa: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(corFundo))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(corTexto)
        )

        Text(
            text = descricao,
            style = MaterialTheme.typography.bodySmall,
            color = colorResource(corTexto)
        )

        if (!motivoRecusa.isNullOrBlank()) {
            Text(
                text = "Motivo da recusa:",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(corTexto)
            )

            Text(
                text = motivoRecusa,
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(corTexto)
            )
        }
    }
}

@Composable
private fun DocumentoCarregamento(
    mensagem: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(R.color.pink_beige))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = colorResource(R.color.main_pink),
            strokeWidth = 2.dp
        )

        Text(
            text = mensagem,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.main_pink)
        )
    }
}