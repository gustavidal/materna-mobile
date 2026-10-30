package com.example.materna.services.materna

import com.example.materna.models.requests.ValidarCodigoRequest
import com.example.materna.models.responses.ValidarCodigoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/codigo")
    fun validarCodigo(
        @Body request: ValidarCodigoRequest
    ): Call<ValidarCodigoResponse>
}