package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: LoginData? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val message: String? = null
)

data class LoginData(
    val token: String,
    val doadora: DoadoraAutenticada
)

data class DoadoraAutenticada(
    val id: Int,
    val nome: String,
    val email: String,

    @SerializedName("is_validado")
    val isValidado: Int
)