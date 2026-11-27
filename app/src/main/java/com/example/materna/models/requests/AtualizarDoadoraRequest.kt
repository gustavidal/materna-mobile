package com.example.materna.models.requests

import com.google.gson.annotations.SerializedName

data class AtualizarDoadoraRequest(
    val nome: String,
    val cpf: String,

    // Foto atual: o backend sobrescreve a coluna com o valor recebido,
    // então precisa ser reenviada para não ser apagada.
    val foto: String? = null,

    @SerializedName("data_nascimento")
    val dataNascimento: String,

    val email: String,
    val logradouro: String,
    val cep: String,
    val bairro: String,
    val numero: Int,
    val complemento: String?,
    val cidade: String,

    @SerializedName("sigla_estado")
    val siglaEstado: String,

    val telefone: String,
    val senha: String
)
