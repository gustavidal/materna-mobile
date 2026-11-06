package com.example.materna.screens.servicos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materna.models.responses.InstituicaoProxima
import com.example.materna.models.responses.InstituicoesProximasResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class ServicosEstado {
    INICIAL,
    CARREGANDO,
    SUCESSO,
    VAZIO,
    ERRO
}

class ServicosScreenViewModel : ViewModel() {
    private val _estado = MutableLiveData<ServicosEstado>()
    val estado: LiveData<ServicosEstado> = _estado

    private val _cep = MutableLiveData<String>()
    val cep: LiveData<String> = _cep

    private val _instituicoes = MutableLiveData<List<InstituicaoProxima>>()
    val instituicoes: LiveData<List<InstituicaoProxima>> = _instituicoes

    private val _mensagemErro = MutableLiveData<String>()
    val mensagemErro: LiveData<String> = _mensagemErro

    private var chamadaAtual: Call<InstituicoesProximasResponse>? = null

    fun onCepChanged(value: String) {
        val numeros = value.filter { it.isDigit() }.take(8)

        val cepFormatado = buildString {
            numeros.forEachIndexed { index, char ->
                if (index == 5) append('-')
                append(char)
            }
        }

        if (cepFormatado == _cep.value) return

        chamadaAtual?.cancel()
        chamadaAtual = null

        _cep.value = cepFormatado
        _instituicoes.value = emptyList()
        _mensagemErro.value = ""
        _estado.value = ServicosEstado.INICIAL
    }

    fun buscarInstituicoes() {
        val cepAtual = _cep.value.orEmpty()

        if (cepAtual.filter { it.isDigit() }.length != 8) {
            _mensagemErro.value = "Informe um CEP válido com 8 dígitos."
            _estado.value = ServicosEstado.ERRO
            return
        }

        chamadaAtual?.cancel()

        _estado.value = ServicosEstado.CARREGANDO
        _mensagemErro.value = ""
        _instituicoes.value = emptyList()

        val call = MaternaRetrofitFactory()
            .getInstituicaoService()
            .buscarInstituicoesProximas(cepAtual)

        chamadaAtual = call

        call.enqueue(object : Callback<InstituicoesProximasResponse> {

            override fun onResponse(
                call: Call<InstituicoesProximasResponse>,
                response: Response<InstituicoesProximasResponse>
            ) {
                if (call !== chamadaAtual) return

                chamadaAtual = null

                val body = response.body()

                if (response.isSuccessful && body?.status == true) {
                    val lista = body.response?.instituicao.orEmpty()

                    _instituicoes.value = lista

                    _estado.value = if (lista.isEmpty()) {
                        ServicosEstado.VAZIO
                    } else {
                        ServicosEstado.SUCESSO
                    }

                    Log.i("Servicos", "Instituições encontradas: ${lista.size}")
                } else {
                    _mensagemErro.value = when (response.code()) {
                        400 -> "O CEP informado é inválido."
                        404 -> "Nenhum banco de leite encontrado para esse CEP."
                        else -> "Não foi possível buscar os bancos de leite."
                    }

                    _estado.value = ServicosEstado.ERRO

                    Log.e(
                        "Servicos",
                        "Erro HTTP ${response.code()}"
                    )
                }
            }

            override fun onFailure(
                call: Call<InstituicoesProximasResponse>,
                t: Throwable
            ) {
                if (call.isCanceled || call !== chamadaAtual) return

                chamadaAtual = null

                _mensagemErro.value =
                    "Não foi possível conectar ao servidor. Verifique sua conexão."

                _estado.value = ServicosEstado.ERRO

                Log.e("Servicos", "Falha: ${t.message}", t)
            }
        })
    }

    override fun onCleared() {
        chamadaAtual?.cancel()
        super.onCleared()
    }
}