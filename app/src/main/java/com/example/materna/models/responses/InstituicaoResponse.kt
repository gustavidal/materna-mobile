package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class InstituicaoResponse(
    val id: Int,
    val nome: String,
    val cnpj: String,
    val email: String,
    val foto: String?,

    @SerializedName("id_coleta")
    val idColeta: Int,

    @SerializedName("id_endereco")
    val idEndereco: Int,

    @SerializedName("tipo_coleta")
    val tipoColeta: String,

    val telefone: String,
    val logradouro: String,
    val cep: String,
    val bairro: String,
    val numero: String,
    val complemento: String?,

    val latitude: String,
    val longitude: String,
    val cidade: String,

    @SerializedName("sigla_estado")
    val siglaEstado: String,

    val jornadas: List<JornadaResponse>
)
