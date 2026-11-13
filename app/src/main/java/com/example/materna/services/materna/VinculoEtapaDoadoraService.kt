package com.example.materna.services.materna

import com.example.materna.models.responses.VinculoEtapaDoadoraResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface VinculoEtapaDoadoraService {
    @GET("vinculo-etapa-doadora/doadora/{id_doadora}")
    fun buscarVinculosDoadora(
        @Path("id_doadora") idDoadora: Int
    ): Call<VinculoEtapaDoadoraResponse>
}
