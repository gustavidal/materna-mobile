package com.example.materna.models.requests

import com.google.gson.annotations.SerializedName

data class CancelarJornadaRequest(
    @SerializedName("id_doadora")
    val idDoadora: Int
)