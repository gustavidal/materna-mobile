package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class AtualizarFotoResponse(
    val response: FotoAtualizada?,
    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val message: String
)

data class FotoAtualizada(
    val id: Int,
    val foto: String?
)