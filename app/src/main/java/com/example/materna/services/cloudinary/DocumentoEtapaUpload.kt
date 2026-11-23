package com.example.materna.services.cloudinary

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.awaitResponse

class DocumentoEtapaUpload {
    suspend fun enviar(
        context: Context,
        uri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = context.contentResolver

            val mimeType = resolver.getType(uri)
                ?: "application/octet-stream"

            val nomeOriginal = resolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(0)
                } else {
                    null
                }
            }?.takeIf { it.isNotBlank() }
                ?: "arquivo"

            val bytes = resolver.openInputStream(uri)?.use {
                it.readBytes()
            } ?: error("Não foi possível ler o arquivo.")

            require(bytes.isNotEmpty()) {
                "O arquivo está vazio."
            }

            require(bytes.size <= 10 * 1024 * 1024) {
                "O arquivo deve ter no máximo 10 MB."
            }

            val arquivo = MultipartBody.Part.createFormData(
                "file",
                nomeOriginal,
                bytes.toRequestBody(
                    mimeType.toMediaTypeOrNull()
                )
            )

            val preset = "materna-jornada"
                .toRequestBody(
                    "text/plain".toMediaTypeOrNull()
                )

            val resposta = CloudinaryRetrofitFactory()
                .getCloudinaryService()
                .enviarDocumento(arquivo, preset)
                .awaitResponse()

            if (!resposta.isSuccessful) {
                error(
                    "Falha no upload: HTTP ${resposta.code()}"
                )
            }

            resposta.body()?.secureUrl
                ?.takeIf { it.isNotBlank() }
                ?: error(
                    "O Cloudinary não retornou a URL."
                )
        }
    }
}