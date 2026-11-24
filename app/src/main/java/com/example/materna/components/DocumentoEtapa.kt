package com.example.materna.components

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.materna.R
import com.example.materna.services.cloudinary.DocumentoEtapaUpload
import kotlinx.coroutines.launch

@Composable
fun DocumentoEtapa(
    modifier: Modifier = Modifier,
    onEnviarDocumento: (String) -> Unit = {}
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
        documentoSelecionado = uri
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                    if (!enviando) {
                        scope.launch {
                            enviando = true
                            mensagem = null
                            urlDocumento = null

                            try {
                                val resultado = upload.enviar(context, uri)

                                resultado.onSuccess { url ->
                                    urlDocumento = url
                                    mensagem = "Documento enviado com sucesso!"
                                    onEnviarDocumento(url)
                                }

                                resultado.onFailure { erro ->
                                    mensagem = erro.message ?: "Não foi possível enviar o documento."
                                }
                            } catch (erro: Exception) {
                                mensagem = erro.message ?: "Erro inesperado no envio."
                            } finally {
                                enviando = false
                            }
                        }
                    }
                },
                enabled = !enviando,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (enviando) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = colorResource(R.color.main_pink),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Enviar documento",
                        color = colorResource(R.color.main_pink)
                    )
                }
            }

            mensagem?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.main_text_color)
                )
            }
        }
    }
}