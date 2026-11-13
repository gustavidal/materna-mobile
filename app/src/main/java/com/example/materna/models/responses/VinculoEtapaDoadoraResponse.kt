package com.example.materna.models.responses

import com.google.gson.annotations.SerializedName

data class VinculoEtapaDoadoraResponse(
    @SerializedName("api_description")
    val apiDescription: String? = null,

    val development: String? = null,
    val version: String? = null,

    val response: VinculoDoadoraData? = null,

    val status: Boolean,

    @SerializedName("status_code")
    val statusCode: Int,

    val count: Int? = null,
    val message: String? = null,
    val field: String? = null
)

data class VinculoDoadoraData(
    @SerializedName("id_doadora")
    val idDoadora: Int,

    @SerializedName("nome_doadora")
    val nomeDoadora: String,

    val jornadas: List<JornadaVinculada> = emptyList()
)

data class JornadaVinculada(
    val id: Int,
    val nome: String,
    val etapas: List<EtapaVinculada> = emptyList(),

    @SerializedName("is_ativo")
    val isAtivo: Int,

    val descricao: String? = null,
    val instituicao: InstituicaoVinculada,

    @SerializedName("total_etapas")
    val totalEtapas: Int = 0,

    @SerializedName("etapas_iniciadas")
    val etapasIniciadas: Int = 0
)

data class InstituicaoVinculada(
    val id: Int,
    val nome: String
)

data class EtapaVinculada(
    val id: Int,
    val ordem: Int,
    val titulo: String,
    val vinculos: List<VinculoEtapa> = emptyList(),
    val descricao: String? = null,
    val documento: String? = null,

    @SerializedName("is_agendavel")
    val isAgendavel: Int,

    @SerializedName("is_repetivel")
    val isRepetivel: Int,

    @SerializedName("is_obrigatorio")
    val isObrigatorio: Int,

    @SerializedName("is_solicita_arquivo")
    val isSolicitaArquivo: Int,

    @SerializedName("is_precisa_aprovacao")
    val isPrecisaAprovacao: Int
)

data class VinculoEtapa(
    val id: Int,
    val ciclo: Int,
    val status: String,
    val encerrado: Int,

    @SerializedName("data_conclusao")
    val dataConclusao: String? = null
)
