package com.example.materna.model

import com.google.gson.annotations.SerializedName

data class CadastroDoadora(
    val nome: String,
    val cpf: String,
    val foto: String? = null,

    @SerializedName("data_nascimento")
    val dataNascimento: String,

    val email: String,
    val senha: String,
    val telefone: String,
    val cep: String,

    @SerializedName("sigla_estado")
    val siglaEstado: String,

    val cidade: String,
    val logradouro: String,
    val bairro: String,
    val numero: Int,
    val complemento: String? = null
)
