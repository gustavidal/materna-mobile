package com.example.materna.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun observeSettings(): Flow<AppSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)

    @Query("UPDATE app_settings SET onboardingCompleted = 1 WHERE id = 1")
    suspend fun markOnboardingCompleted()

    @Query("UPDATE app_settings SET emailPendente = :email WHERE id = 1")
    suspend fun atualizarEmailPendente(email: String)

    @Transaction
    suspend fun salvarEmailPendente(email: String) {
        val settings = observeSettings().first() ?: AppSettings()

        saveSettings(
            settings.copy(emailPendente = email)
        )
    }

    @Query("UPDATE app_settings SET emailPendente = NULL WHERE id = 1")
    suspend fun limparEmailPendente()

    @Transaction
    suspend fun salvarIdDoadora(idDoadora: Int) {
        require(idDoadora > 0) {
            "ID da doadora inválido."
        }

        val settings = observeSettings().first() ?: AppSettings()

        saveSettings(
            settings.copy(idDoadora = idDoadora)
        )
    }

    @Query("UPDATE app_settings SET idDoadora = NULL WHERE id = 1")
    suspend fun limparIdDoadora()
}