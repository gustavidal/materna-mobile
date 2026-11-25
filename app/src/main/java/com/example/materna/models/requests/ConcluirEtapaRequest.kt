package com.example.materna.models.requests

import com.google.gson.annotations.SerializedName

data class ConcluirEtapaRequest(
    @SerializedName("data_conclusao")
    val dataConclusao: String
)