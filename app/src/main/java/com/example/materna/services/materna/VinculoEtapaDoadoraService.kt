package com.example.materna.services.materna

import com.example.materna.models.requests.CancelarJornadaRequest
import com.example.materna.models.requests.ConcluirEtapaRequest
import com.example.materna.models.requests.CriarVinculoEtapaRequest
import com.example.materna.models.responses.CriarVinculoEtapaResponse
import com.example.materna.models.responses.VinculoEtapaDoadoraResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VinculoEtapaDoadoraService {
    @Headers("Content-Type: application/json")
    @POST("vinculo-etapa-doadora")
    fun criarVinculoEtapa(
        @Body request: CriarVinculoEtapaRequest
    ): Call<CriarVinculoEtapaResponse>

    @GET("vinculo-etapa-doadora/doadora/{id_doadora}")
    fun buscarVinculosDoadora(
        @Path("id_doadora") idDoadora: Int
    ): Call<VinculoEtapaDoadoraResponse>

    @Headers("Content-Type: application/json")
    @PATCH("vinculo-etapa-doadora/{id}")
    fun concluirEtapa(
        @Path("id") idVinculo: Int,
        @Body request: ConcluirEtapaRequest
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("vinculo-etapa-doadora/{id}/cancelar")
    fun cancelarJornada(
        @Path("id") idVinculo: Int,
        @Body request: CancelarJornadaRequest
    ): Call<ResponseBody>
}
