package com.example.materna.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MaternaRetrofitFactory {
    private val BASE_URL = "http://10.0.2.2:9090/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getDoadoraService(): DoadoraService {
        return retrofit.create(DoadoraService::class.java)
    }
}
