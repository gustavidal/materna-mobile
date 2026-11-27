package com.example.materna.screens.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.materna.data.local.AppSettingsDao
import com.example.materna.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SplashScreenViewModel(
    private val settingsDao: AppSettingsDao,
    context: Context,
) : ViewModel() {

    private val sessionManager =
        SessionManager(
            context.applicationContext,
        )

    suspend fun verificarDestino(): String {
        return withContext(Dispatchers.IO) {
            val settings = settingsDao.observeSettings().first()

            when {
                sessionManager.tokenValido() -> {
                    "dashboard"
                }

                settings?.idDoadoraPendente != null &&
                    settings.idDoadoraPendente > 0 &&
                    !settings.emailPendente.isNullOrBlank() -> {
                    "confirmar-email/${settings.idDoadoraPendente}"
                }

                settings?.onboardingCompleted == true -> {
                    "login"
                }

                else -> {
                    "onboarding"
                }
            }
        }
    }
}
