package com.example.materna.models.requests

import com.google.gson.annotations.SerializedName

data class ValidarCodigoRequest(
    @SerializedName("id_doadora")
    val idDoadora: Int,

    val codigo: Int
)