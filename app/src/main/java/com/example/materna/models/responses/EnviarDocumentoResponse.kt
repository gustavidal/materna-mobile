package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class EnviarDocumentoResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: DocumentoResponse? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val message: String
)

data class DocumentoResponse(
    val documento: String,

    @SerializedName("id_doadora")
    val idDoadora: Int,

    @SerializedName("id_jornada_doacao")
    val idJornadaDoacao: Int,

    @SerializedName("id_documento")
    val idDocumento: Int,

    @SerializedName("id_status_documento")
    val idStatusDocumento: Int,

    val status: String
)
