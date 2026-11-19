package com.example.materna.services.materna

import com.example.materna.models.requests.CriarVinculoEtapaRequest
import com.example.materna.models.responses.CriarVinculoEtapaResponse
import com.example.materna.models.responses.VinculoEtapaDoadoraResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VinculoEtapaDoadoraService {
    @POST("vinculo-etapa-doadora")
    fun criarVinculoEtapa(
        @Body request: CriarVinculoEtapaRequest
    ): Call<CriarVinculoEtapaResponse>

    @GET("vinculo-etapa-doadora/doadora/{id_doadora}")
    fun buscarVinculosDoadora(
        @Path("id_doadora") idDoadora: Int
    ): Call<VinculoEtapaDoadoraResponse>
}
