package com.example.materna.screens.dashboard

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materna.models.responses.CampanhaApiResponse
import com.example.materna.models.responses.CampanhaResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.format.DateTimeParseException

class DashboardScreenViewModel(
    context: Context
) : ViewModel() {
    private val applicationContext = context.applicationContext

    private val _nomeDoadora = MutableLiveData<String>()
    val nomeDoadora: LiveData<String> = _nomeDoadora

    private val _bancoSelecionado = MutableLiveData<Boolean>()
    val bancoSelecionado: LiveData<Boolean> = _bancoSelecionado

    private val _campanhas = MutableLiveData<List<CampanhaResponse>>()
    val campanhas: LiveData<List<CampanhaResponse>> = _campanhas

    private val _carregandoCampanhas = MutableLiveData<Boolean>()
    val carregandoCampanhas: LiveData<Boolean> = _carregandoCampanhas

    private val _mensagemCampanhas = MutableLiveData<String?>()
    val mensagemCampanhas: LiveData<String?> = _mensagemCampanhas

    private var chamadaCampanhas: Call<CampanhaApiResponse>? = null

    fun atualizarNome(nome: String) {
        _nomeDoadora.value = nome
    }

    fun atualizarBancoSelecionado(selecionado: Boolean) {
        _bancoSelecionado.value = selecionado
    }

    private fun campanhaVigente(campanha: CampanhaResponse): Boolean {
        return try {
            val agora = Instant.now()
            val inicio = Instant.parse(campanha.dataInicio)
            val fim = Instant.parse(campanha.dataFim)

            campanha.isAtivo == 1 &&
                    !agora.isBefore(inicio) &&
                    !agora.isAfter(fim)
        } catch (e: DateTimeParseException) {
            Log.e("Campanhas", "Data inválida na campanha ${campanha.id}", e)
            false
        }
    }

    fun buscarCampanhas() {
        if (_carregandoCampanhas.value == true) return

        _carregandoCampanhas.value = true
        _mensagemCampanhas.value = null

        val call = MaternaRetrofitFactory(applicationContext)
            .getCampanhaService()
            .listarCampanhas()

        chamadaCampanhas = call

        call.enqueue(object : Callback<CampanhaApiResponse> {
            override fun onResponse(
                call: Call<CampanhaApiResponse>,
                response: Response<CampanhaApiResponse>
            ) {
                if (call.isCanceled) return

                _carregandoCampanhas.value = false

                Log.i("Campanhas", "Response: ${response.body()}")

                if (response.isSuccessful) {
                    val resultado = response.body()

                    if (resultado?.status == true) {
                        _campanhas.value = resultado.response
                            ?.campanha
                            .orEmpty()
                            .filter { campanhaVigente(it) }
                            .sortedByDescending { it.dataInicio }

                        _mensagemCampanhas.value = null
                    } else {
                        _campanhas.value = emptyList()
                        _mensagemCampanhas.value = resultado?.message ?: "Não foi possível carregar as campanhas."
                    }
                } else {
                    _campanhas.value = emptyList()

                    if (response.code() == 404) {
                        _mensagemCampanhas.value = null
                        return
                    }

                    val erro = try {
                        response.errorBody()?.string()?.let {
                            Gson().fromJson(it, CampanhaApiResponse::class.java)
                        }
                    } catch (e: Exception) {
                        null
                    }

                    _mensagemCampanhas.value = erro?.message ?: "Erro ao buscar campanhas (HTTP ${response.code()})."

                    Log.e("Campanhas", "Erro HTTP ${response.code()}: ${_mensagemCampanhas.value}")
                }
            }

            override fun onFailure(
                call: Call<CampanhaApiResponse>,
                t: Throwable
            ) {
                if (call.isCanceled) return

                _carregandoCampanhas.value = false
                _campanhas.value = emptyList()

                _mensagemCampanhas.value = "Não foi possível conectar à API. Verifique se o backend está ativo."

                Log.e("Campanhas", "Falha: ${t.message}", t)
            }
        })
    }

    override fun onCleared() {
        chamadaCampanhas?.cancel()
        super.onCleared()
    }
}