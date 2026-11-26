package com.example.materna.models.requests

import com.google.gson.annotations.SerializedName

data class AgendamentoRequest(
    @SerializedName("id_cadastro_horario")
    val idCadastroHorario: Int,

    @SerializedName("id_jornada_doacao")
    val idJornadaDoacao: Int,

    @SerializedName("id_status_agendamento")
    val idStatusAgendamento: Int = 1
)