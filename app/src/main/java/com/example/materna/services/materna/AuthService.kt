package com.example.materna.services.materna

import com.example.materna.models.requests.ValidarCodigoRequest
import com.example.materna.models.responses.ValidarCodigoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("doadora/otp/verificar")
    fun validarCodigo(
        @Body request: ValidarCodigoRequest
    ): Call<ValidarCodigoResponse>
}