package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class AgendamentoResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: AgendamentoCriadoResponse? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val message: String
)

data class AgendamentoCriadoResponse(
    val id: Int,

    @SerializedName("id_cadastro_horario")
    val idCadastroHorario: Int,

    @SerializedName("id_jornada_doacao")
    val idJornadaDoacao: Int,

    @SerializedName("id_status_agendamento")
    val idStatusAgendamento: Int
)