package com.example.materna.screens.perfil

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.materna.data.local.AppSettingsDao
import com.example.materna.models.responses.PerfilDoadora
import com.example.materna.models.responses.PerfilDoadoraResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilScreenViewModel(
    private val settingsDao: AppSettingsDao
) : ViewModel() {
    private val _doadora = MutableLiveData<PerfilDoadora?>()
    val doadora: LiveData<PerfilDoadora?> = _doadora

    private val _carregando = MutableLiveData(false)
    val carregando: LiveData<Boolean> = _carregando

    private val _mensagemErro = MutableLiveData<String?>()
    val mensagemErro: LiveData<String?> = _mensagemErro

    private var chamadaAtual: Call<PerfilDoadoraResponse>? = null

    init {
        carregarPerfil()
    }

    private fun carregarPerfil() {
        viewModelScope.launch {
            try {
                val settings = settingsDao.observeSettings().first()
                val idDoadora = settings?.idDoadora

                if (idDoadora != null && idDoadora > 0) {
                    buscarDoadora(idDoadora)
                } else {
                    _mensagemErro.value = "Nenhuma doadora identificada. Faça seu cadastro ou entre na sua conta."
                }
            } catch (e: Exception) {
                Log.e("PerfilDoadora", "Erro ao recuperar ID da doadora", e)

                _mensagemErro.value = "Não foi possível recuperar os dados da doadora."
            }
        }
    }

    fun buscarDoadora(id: Int) {
        if (id <= 0) {
            _mensagemErro.value = "ID da doadora inválido."
            return
        }

        chamadaAtual?.cancel()

        _carregando.value = true
        _mensagemErro.value = null

        val call = MaternaRetrofitFactory().getDoadoraService()
            .buscarDoadora(id)

        chamadaAtual = call

        call.enqueue(object : Callback<PerfilDoadoraResponse> {
            override fun onResponse(
                call: Call<PerfilDoadoraResponse>,
                response: Response<PerfilDoadoraResponse>
            ) {
                if (call !== chamadaAtual) return

                _carregando.value = false

                Log.i(
                    "PerfilDoadora",
                    "Response: ${response.body()}"
                )

                val resultado = response.body()

                if (response.isSuccessful && resultado?.status == true) {
                    val doadoraEncontrada = resultado.response
                        ?.doadora
                        ?.firstOrNull { it.id == id }

                    if (doadoraEncontrada != null) {
                        _doadora.value = doadoraEncontrada
                        _mensagemErro.value = null
                    } else {
                        _doadora.value = null
                        _mensagemErro.value = "Doadora não encontrada."
                    }
                } else {
                    _doadora.value = null
                    _mensagemErro.value = resultado?.message ?: "Erro ao consultar perfil (HTTP ${response.code()})."

                    Log.e(
                        "PerfilDoadora",
                        "Erro HTTP ${response.code()}: ${_mensagemErro.value}"
                    )
                }
            }

            override fun onFailure(
                call: Call<PerfilDoadoraResponse>,
                t: Throwable
            ) {
                if (call.isCanceled || call !== chamadaAtual) return

                _carregando.value = false
                _doadora.value = null

                _mensagemErro.value = "Não foi possível conectar à API. Verifique se o backend está ativo."

                Log.e(
                    "PerfilDoadora",
                    "Falha: ${t.message}",
                    t
                )
            }
        })
    }

    override fun onCleared() {
        chamadaAtual?.cancel()
        super.onCleared()
    }
}