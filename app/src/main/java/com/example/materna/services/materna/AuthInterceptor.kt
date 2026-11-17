package com.example.materna.services.materna

import com.example.materna.session.SessionEvents
import com.example.materna.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestOriginal = chain.request()
        val requestBuilder = requestOriginal.newBuilder()

        val token = if (sessionManager.tokenValido()) {
            sessionManager.obterToken()
        } else {
            null
        }

        if (!token.isNullOrBlank()) {
            requestBuilder.header(
                "Authorization",
                "Bearer $token"
            )
        }

        val response = chain.proceed(requestBuilder.build())

        val rota = requestOriginal.url.encodedPath

        val rotaPublica = rota.endsWith("/doadora/login")

        if (
            response.code == 401 &&
            !rotaPublica &&
            !token.isNullOrBlank()
        ) {
            val tokenAtual = sessionManager.obterToken()

            if (tokenAtual == token) {
                sessionManager.limparSessao()
                SessionEvents.notificarSessaoExpirada()
            }
        }

        return response
    }
}
