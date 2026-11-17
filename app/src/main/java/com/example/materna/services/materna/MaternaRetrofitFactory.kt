package com.example.materna.services.materna

import android.content.Context
import com.example.materna.session.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MaternaRetrofitFactory(context: Context) {
    private val _baseUrl = "http://10.0.2.2:9090/v1/materna/"

    private val sessionManager = SessionManager(
        context.applicationContext
    )

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sessionManager))
        .build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(_baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()





    fun getAuthService(): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    fun getCampanhaService(): CampanhaService {
        return retrofit.create(CampanhaService::class.java)
    }

    fun getDoadoraService(): DoadoraService {
        return retrofit.create(DoadoraService::class.java)
    }

    fun getInstituicaoService(): InstituicaoService {
        return retrofit.create(InstituicaoService::class.java)
    }

    fun getVinculoEtapaDoadoraService(): VinculoEtapaDoadoraService {
        return retrofit.create(VinculoEtapaDoadoraService::class.java)
    }
}
