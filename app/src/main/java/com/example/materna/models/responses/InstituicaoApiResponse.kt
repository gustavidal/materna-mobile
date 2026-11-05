package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class InstituicaoApiResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: InstituicaoDataResponse? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val message: String? = null,
    val field: String? = null
)

data class InstituicaoDataResponse(
    val instituicao: List<InstituicaoResponse> = emptyList()
)
