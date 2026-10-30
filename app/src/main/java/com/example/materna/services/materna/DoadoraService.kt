package com.example.materna.services.materna

import com.example.materna.models.requests.CadastroDoadora
import com.example.materna.models.responses.CadastroDoadoraResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DoadoraService {
    @Headers("Content-Type: application/json")
    @POST("doadora")
    fun cadastrarDoadora(
        @Body doadora: CadastroDoadora
    ): Call<CadastroDoadoraResponse>
}