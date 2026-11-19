package com.example.materna.models.requests

import com.google.gson.annotations.SerializedName

data class CriarVinculoEtapaRequest(
    @SerializedName("id_doadora")
    val idDoadora: Int,

    @SerializedName("id_etapa")
    val idEtapa: Int,

    @SerializedName("id_instituicao")
    val idInstituicao: Int,

    @SerializedName("id_jornada")
    val idJornada: Int
)
