package com.example.materna.service

import com.example.materna.model.Endereco
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface EnderecoService {
    @GET("{cep}/json/")
    fun getEnderecoByCep(
        @Path("cep") cep: String
    ): Call<Endereco>
}