package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class InstituicoesProximasResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: InstituicoesProximasData? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val count: Int? = null
)

data class InstituicoesProximasData(
    val instituicao: List<InstituicaoProxima>
)

data class InstituicaoProxima(
    val id: Int,
    val nome: String,
    val foto: String? = null,
    val logradouro: String,
    val numero: String,
    val bairro: String,
    val latitude: String,
    val longitude: String,

    @SerializedName("tipo_coleta")
    val tipoColeta: String,

    @SerializedName("distancia_km")
    val distanciaKm: Double
)