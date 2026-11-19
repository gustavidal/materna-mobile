package com.example.materna.services.materna

import com.example.materna.models.requests.AtualizarDoadoraRequest
import com.example.materna.models.requests.AtualizarFotoRequest
import com.example.materna.models.requests.CadastroDoadora
import com.example.materna.models.requests.RedefinirSenhaRequest
import com.example.materna.models.responses.AtualizarDoadoraResponse
import com.example.materna.models.responses.AtualizarFotoResponse
import com.example.materna.models.responses.CadastroDoadoraResponse
import com.example.materna.models.responses.PerfilDoadoraResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DoadoraService {
    @Headers("Content-Type: application/json")
    @POST("doadora")
    fun cadastrarDoadora(
        @Body doadora: CadastroDoadora
    ): Call<CadastroDoadoraResponse>

    @Headers("Content-Type: application/json")
    @PATCH("doadora/senha/{id}")
    fun redefinirSenha(
        @Path("id") idDoadora: Int,
        @Body request: RedefinirSenhaRequest
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @PUT("doadora/{id}")
    fun atualizarDoadora(
        @Path("id") idDoadora: Int,
        @Body doadora: AtualizarDoadoraRequest
    ): Call<AtualizarDoadoraResponse>

    @GET("doadora/{id}")
    fun buscarDoadora(
        @Path("id") id: Int
    ): Call<PerfilDoadoraResponse>

    @PATCH("doadora/me")
    fun atualizarFoto(
        @Body request: AtualizarFotoRequest
    ): Call<AtualizarFotoResponse>
}