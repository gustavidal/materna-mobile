package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class JornadaResponse(
    val id: Int,
    val nome: String,
    val descricao: String?,
    val etapas: List<EtapaResponse>,

    @SerializedName("is_ativo")
    val isAtivo: Int
)
