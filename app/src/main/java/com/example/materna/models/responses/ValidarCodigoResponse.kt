package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class ValidarCodigoResponse (
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: AuthCodigoResponse? = null,

    val status: Boolean,
    @SerializedName("status_code")
    val statusCode: Int,

    val message: String,
    val field: String? = null
)

data class AuthCodigoResponse (
    val codigo: Any
)