package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class CriarVinculoEtapaResponse(
    val response: VinculoCriado? = null,
    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val message: String? = null,
    val field: String? = null
)

data class VinculoCriado(
    @SerializedName("id_doadora")
    val idDoadora: Int,

    @SerializedName("id_etapa")
    val idEtapa: Int,

    @SerializedName("id_instituicao")
    val idInstituicao: Int,

    @SerializedName("id_jornada")
    val idJornada: Int,

    val id: Int
)
