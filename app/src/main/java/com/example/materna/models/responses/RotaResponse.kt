package com.example.materna.models.responses

data class RotaResponse(
    val code: String,
    val routes: List<Rota>
)

data class Rota(
    val distance: Double,
    val duration: Double,
    val geometry: GeometriaRota
)

data class GeometriaRota(
    val coordinates: List<List<Double>>,
    val type: String
)