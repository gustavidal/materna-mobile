package com.example.materna.services.viacep

import com.example.materna.models.responses.Endereco
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface EnderecoService {
    @GET("{cep}/json/")
    fun getEnderecoByCep(
        @Path("cep") cep: String
    ): Call<Endereco>
}