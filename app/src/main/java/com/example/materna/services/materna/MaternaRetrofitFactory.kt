package com.example.materna.services.materna

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MaternaRetrofitFactory {
    private val _baseUrl = "http://10.0.2.2:9090/v1/materna/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(_baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getAuthService(): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    fun getCampanhaService(): CampanhaService {
        return retrofit.create(CampanhaService::class.java)
    }

    fun getDoadoraService(): DoadoraService {
        return retrofit.create(DoadoraService::class.java)
    }

    fun getInstituicaoService(): InstituicaoService {
        return retrofit.create(InstituicaoService::class.java)
    }
}