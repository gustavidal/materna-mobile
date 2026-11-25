package com.example.materna.services.materna

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HorarioService {
    @GET("horario/agenda")
    fun buscarHorariosDisponiveis(
        @Query("id_instituicao")
        idInstituicao: Int,

        @Query("is_proximo")
        isProximo: Boolean = true,

        @Query("id_doadora")
        idDoadora: Int? = null
    ): Call<ResponseBody>
}