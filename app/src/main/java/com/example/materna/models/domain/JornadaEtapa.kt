package com.example.materna.models.domain

enum class JornadaStatus {
    CONCLUIDA,
    ATUAL,
    BLOQUEADA
}

data class JornadaEtapa(
    val id: String,
    val titulo: String,
    val descricao: String,
    val status: JornadaStatus,

    // Propriedades das etapas da instituição
    val isAgendavel: Int? = null,
    val isRepetivel: Int? = null,
    val isObrigatorio: Int? = null,
    val isSolicitaArquivo: Int? = null,
    val isPrecisaAprovacao: Int? = null,
    val documento: String? = null
)