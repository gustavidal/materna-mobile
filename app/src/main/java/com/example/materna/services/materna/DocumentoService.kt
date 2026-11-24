package com.example.materna.services.materna

import com.example.materna.models.requests.EnviarDocumentoRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface DocumentoService {
    @Headers("Content-Type: application/json")
    @POST("documento/{id_jornada_doacao}")
    fun enviarDocumento(
        @Path("id_jornada_doacao") idVinculo: Int,
        @Body request: EnviarDocumentoRequest
    ): Call<ResponseBody>
}