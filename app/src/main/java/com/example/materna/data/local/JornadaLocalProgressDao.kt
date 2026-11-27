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

    @Query(
        """
    DELETE FROM jornada_local_progress
    WHERE idDoadora = :idDoadora
    AND idJornada = :idJornada
    """
    )
    suspend fun limparProgresso(
        idDoadora: Int,
        idJornada: Int
    ): Int

    // Marcador local de "jornada concluída" (idEtapa = jornada_concluida_<idInstituicao>).
    // Permite mostrar a tela de jornada finalizada mesmo depois que a API
    // deixa de listar a jornada (vínculos encerrados pela instituição).
    @Query(
        """
        SELECT * FROM jornada_local_progress
        WHERE idDoadora = :idDoadora
        AND idEtapa LIKE 'jornada_concluida_%'
        ORDER BY dataConclusao DESC
        LIMIT 1
        """
    )
    suspend fun buscarMarcadorFinalizacao(
        idDoadora: Int
    ): JornadaLocalProgress?

    @Query(
        """
        SELECT * FROM jornada_local_progress
        WHERE idDoadora = :idDoadora
        AND idJornada = :idJornada
        AND idEtapa LIKE 'jornada_concluida_%'
        LIMIT 1
        """
    )
    suspend fun buscarMarcadorFinalizacaoDaJornada(
        idDoadora: Int,
        idJornada: Int
    ): JornadaLocalProgress?
}
