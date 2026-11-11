package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class CampanhaApiResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: CampanhaDataResponse? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val message: String? = null,
    val count: Int? = null
)

data class CampanhaDataResponse(
    val campanha: List<CampanhaResponse> = emptyList()
)
