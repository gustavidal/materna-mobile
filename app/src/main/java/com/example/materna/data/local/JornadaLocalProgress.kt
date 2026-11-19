package com.example.materna.data.local

import androidx.room.Entity

@Entity(
    tableName = "jornada_local_progress",
    primaryKeys = [
        "idDoadora",
        "idJornada",
        "idEtapa"
    ]
)
data class JornadaLocalProgress(
    val idDoadora: Int,
    val idJornada: Int,
    val idEtapa: String,
    val concluida: Boolean = true,
    val dataConclusao: Long = System.currentTimeMillis()
)
