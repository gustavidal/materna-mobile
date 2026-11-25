package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class DocumentosDoadoraResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: List<DocumentoDoadoraResponse>? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val count: Int? = null,
    val message: String? = null
)

data class DocumentoDoadoraResponse(
    @SerializedName("id_documento")
    val idDocumento: Int,

    val documento: String,

    @SerializedName("motivo_recusa")
    val motivoRecusa: String? = null,

    @SerializedName("id_status_documento")
    val idStatusDocumento: Int,

    val status: String,

    @SerializedName("id_jornada_doacao")
    val idJornadaDoacao: Int,

    @SerializedName("id_doadora")
    val idDoadora: Int
)