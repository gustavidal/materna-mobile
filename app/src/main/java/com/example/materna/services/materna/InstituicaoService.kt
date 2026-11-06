package com.example.materna.services.materna

import com.example.materna.models.responses.InstituicaoApiResponse
import com.example.materna.models.responses.InstituicoesProximasResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InstituicaoService {
    @GET("instituicao/{id}")
    fun buscarInstituicao(
        @Path("id") id: Int
    ): Call<InstituicaoApiResponse>

    @GET("instituicao/proximas")
    fun buscarInstituicoesProximas(
        @Query("cep") cep: String
    ): Call<InstituicoesProximasResponse>
}
