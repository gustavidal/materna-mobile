package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class HorariosAgendaResponse(
    val response: AgendaResponse? = null,
    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val count: Int? = null,
    val message: String? = null
)

data class AgendaResponse(
    val horario: List<HorarioAgenda>
)

data class HorarioAgenda(
    @SerializedName("id_horario")
    val idHorario: Int,

    val data: String,
    val hora: String,
    val disponivel: Int,

    @SerializedName("id_status")
    val idStatus: Int?,

    val status: String?,

    @SerializedName("id_etapa")
    val idEtapa: Int,

    val etapa: String,

    @SerializedName("id_jornada")
    val idJornada: Int,

    @SerializedName("id_agendamento")
    val idAgendamento: Int?,

    @SerializedName("id_jornada_doacao")
    val idJornadaDoacao: Int?,

    val encerrado: Int?,

    @SerializedName("id_doadora")
    val idDoadora: Int?,

    @SerializedName("is_proximo")
    val isProximo: Int
)