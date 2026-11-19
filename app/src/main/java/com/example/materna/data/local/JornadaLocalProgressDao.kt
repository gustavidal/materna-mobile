package com.example.materna.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JornadaLocalProgressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun salvarConclusao(
        progresso: JornadaLocalProgress
    ): Long

    @Query(
        """
        SELECT * FROM jornada_local_progress
        WHERE idDoadora = :idDoadora
        AND idJornada = :idJornada
        """
    )
    fun observarProgresso(
        idDoadora: Int,
        idJornada: Int
    ): Flow<List<JornadaLocalProgress>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM jornada_local_progress
            WHERE idDoadora = :idDoadora
            AND idJornada = :idJornada
            AND idEtapa = :idEtapa
            AND concluida = 1
        )
        """
    )
    suspend fun etapaConcluida(
        idDoadora: Int,
        idJornada: Int,
        idEtapa: String
    ): Boolean
}
