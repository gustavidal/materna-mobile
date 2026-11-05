package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class EtapaResponse(
    val id: Int,
    val ordem: Int,
    val titulo: String,
    val descricao: String,
    val documento: String?,

    @SerializedName("id_jornada")
    val idJornada: Int,

    @SerializedName("is_agendavel")
    val isAgendavel: Int,

    @SerializedName("is_repetivel")
    val isRepetivel: Int,

    @SerializedName("is_obrigatorio")
    val isObrigatorio: Int,

    @SerializedName("is_solicita_arquivo")
    val isSolicitaArquivo: Int,

    @SerializedName("is_precisa_aprovacao")
    val isPrecisaAprovacao: Int
)
