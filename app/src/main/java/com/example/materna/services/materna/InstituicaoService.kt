package com.example.materna.services.materna

import com.example.materna.models.responses.InstituicaoApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface InstituicaoService {
    @GET("instituicao/{id}")
    fun buscarInstituicao(
        @Path("id") id: Int
    ): Call<InstituicaoApiResponse>
}
