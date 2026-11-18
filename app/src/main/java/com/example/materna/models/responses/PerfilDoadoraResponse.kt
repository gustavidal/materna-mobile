package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class PerfilDoadoraResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: PerfilDoadoraData? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val message: String? = null,
    val field: String? = null
)

data class PerfilDoadoraData(
    val doadora: List<PerfilDoadora> = emptyList()
)

data class PerfilDoadora(
    val id: Int,
    val nome: String,
    val cpf: String,
    val foto: String? = null,

    @SerializedName("data_nascimento")
    val dataNascimento: String,

    val email: String,

    @SerializedName("is_validado")
    val isValidado: Int,

    @SerializedName("id_endereco")
    val idEndereco: Int,

    @SerializedName("id_telefone")
    val idTelefone: Int,

    val logradouro: String,
    val cep: String,
    val bairro: String,
    val numero: String,
    val complemento: String? = null,

    val latitude: String? = null,
    val longitude: String? = null,

    val cidade: String,

    @SerializedName("sigla_estado")
    val siglaEstado: String,

    val telefone: String,

    @SerializedName("etapas_concluidas")
    val etapasConcluidas: Int = 0,

    @SerializedName("total_etapas")
    val totalEtapas: Int = 0,

    @SerializedName("doacoes_realizadas")
    val doacoesRealizadas: Int = 0,

    @SerializedName("total_agendamentos")
    val totalAgendamentos: Int = 0
)
