package com.example.materna.service

import com.example.materna.model.CadastroDoadora
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface DoadoraService {
    @POST("v1/materna/doadora")
    fun cadastrarDoadora(
        @Body doadora: CadastroDoadora
    ): Call<Void>
}
