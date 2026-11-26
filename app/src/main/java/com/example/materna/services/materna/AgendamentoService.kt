package com.example.materna.services.materna

import com.example.materna.models.requests.AgendamentoRequest
import com.example.materna.models.requests.CancelarAgendamentoRequest
import com.example.materna.models.responses.AgendamentoResponse
import com.example.materna.models.responses.CancelarAgendamentoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AgendamentoService {
    @Headers("Content-Type: application/json")
    @POST("agendamento")
    fun criarAgendamento(
        @Body request: AgendamentoRequest
    ): Call<AgendamentoResponse>

    @Headers("Content-Type: application/json")
    @PUT("agendamento/{id}")
    fun cancelarAgendamento(
        @Path("id") idAgendamento: Int,
        @Body request: CancelarAgendamentoRequest
    ): Call<CancelarAgendamentoResponse>
}