package com.example.materna.model

import com.google.gson.annotations.SerializedName

data class Endereco (
    val cep: String = "",
    val logradouro: String = "",
    @SerializedName("localidade") val cidade: String = "",
    val bairro: String = "",
    val uf: String = "",
    val numero: Int,
    val complemento: String,
    val erro: Boolean? = false
)