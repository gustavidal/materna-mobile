package com.example.materna.services.osrm

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OsrmRetrofitFactory {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://router.project-osrm.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getRotaService(): RotaService {
        return retrofit.create(RotaService::class.java)
    }
}
