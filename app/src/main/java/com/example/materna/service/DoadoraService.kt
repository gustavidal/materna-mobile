package com.example.materna.service

import com.example.materna.model.CadastroDoadora
import com.example.materna.model.CadastroDoadoraResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DoadoraService {
    @Headers("Content-Type: application/json")
    @POST("v1/materna/doadora")
    fun cadastrarDoadora(
        @Body doadora: CadastroDoadora
    ): Call<CadastroDoadoraResponse>
}
