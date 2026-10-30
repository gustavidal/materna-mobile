package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class CadastroDoadoraResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: DoadoraResponse? = null,

    val status: Boolean,
    @SerializedName("status_code")
    val statusCode: Int,

    val message: String,
    val field: String? = null
)

data class DoadoraResponse(
    val nome: String,
    val cpf: String,

    @SerializedName("data_nascimento")
    val dataNascimento: String,

    val email: String,
    val telefone: String,
    val cep: String,

    @SerializedName("sigla_estado")
    val siglaEstado: String,

    val cidade: String,
    val logradouro: String,
    val bairro: String,
    val numero: String,
    val complemento: String?,

    val latitude: Double?,
    val longitude: Double?,
    val foto: String?,
    val id: Int
)
