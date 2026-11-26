package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class CancelarAgendamentoResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: AgendamentoCanceladoResponse? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val message: String
)

data class AgendamentoCanceladoResponse(
    val id: Int,

    @SerializedName("id_status_agendamento")
    val idStatusAgendamento: Int,

    @SerializedName("id_doadora")
    val idDoadora: Int
)