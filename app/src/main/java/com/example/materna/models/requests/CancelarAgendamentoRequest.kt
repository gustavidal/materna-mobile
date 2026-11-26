package com.example.materna.models.requests

import com.google.gson.annotations.SerializedName

data class CancelarAgendamentoRequest(
    @SerializedName("id_doadora")
    val idDoadora: Int,

    @SerializedName("id_status_agendamento")
    val idStatusAgendamento: Int = 4
)