package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class AtualizarDoadoraResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: DoadoraAtualizada? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val message: String? = null,
    val field: String? = null
)

data class DoadoraAtualizada(
    val id: Int,
    val nome: String,
    val cpf: String,
    val foto: String? = null,

    @SerializedName("data_nascimento")
    val dataNascimento: String,

    val email: String,
    val logradouro: String,
    val cep: String,
    val bairro: String,
    val numero: String,
    val complemento: String? = null,

    val latitude: Double? = null,
    val longitude: Double? = null,

    val cidade: String,

    @SerializedName("sigla_estado")
    val siglaEstado: String,

    val telefone: String
)
