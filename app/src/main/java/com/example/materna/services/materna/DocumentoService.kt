package com.example.materna.services.materna

import com.example.materna.models.requests.EnviarDocumentoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface DocumentoService {
    @POST("documento/{id_jornada_doacao}")
    suspend fun enviarDocumento(
        @Path("id_jornada_doacao") idVinculo: Int,
        @Body request: EnviarDocumentoRequest
    ): Response<Void>
}