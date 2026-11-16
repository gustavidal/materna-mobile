package com.example.materna.services.osrm

import com.example.materna.models.responses.RotaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RotaService {
    @GET("route/v1/driving/{coordenadas}")
    fun buscarRota(
        @Path(value = "coordenadas", encoded = true) coordenadas: String,
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "geojson"
    ): Call<RotaResponse>
}