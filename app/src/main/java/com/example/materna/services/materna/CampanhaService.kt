package com.example.materna.services.materna

import com.example.materna.models.responses.CampanhaApiResponse
import retrofit2.Call
import retrofit2.http.GET

interface CampanhaService {
    @GET("campanha")
    fun listarCampanhas(): Call<CampanhaApiResponse>
}
