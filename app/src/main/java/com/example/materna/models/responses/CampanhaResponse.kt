package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class CampanhaResponse(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val foto: String? = null,

    @SerializedName("data_inicio")
    val dataInicio: String,

    @SerializedName("data_fim")
    val dataFim: String,

    @SerializedName("is_ativo")
    val isAtivo: Int,

    @SerializedName("id_instituicao")
    val idInstituicao: Int,

    @SerializedName("id_funcionario")
    val idFuncionario: Int
)
