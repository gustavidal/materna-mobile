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
    val status: JornadaStatus
)