package com.example.materna.models.requests

import com.google.gson.annotations.SerializedName

data class EnviarDocumentoRequest(
    @SerializedName("id_doadora")
    val idDoadora: Int,

    @SerializedName("documento")
    val documento: String
)
