package com.example.materna.screens.confirmarbanco

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materna.models.responses.VinculoEtapaDoadoraResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class ConfirmarBancoEstado {
    INICIAL,
    CARREGANDO,
    SEM_VINCULO,
    MESMO_BANCO,
    OUTRO_BANCO,
    ERRO
}

class ConfirmarBancoScreenViewModel : ViewModel() {
    private val _estado = MutableLiveData(
        ConfirmarBancoEstado.INICIAL
    )
    val estado: LiveData<ConfirmarBancoEstado> = _estado

    private val _mensagemErro = MutableLiveData("")
    val mensagemErro: LiveData<String> = _mensagemErro

    private var chamadaAtual: Call<VinculoEtapaDoadoraResponse>? = null

    fun verificarVinculo(
        idDoadora: Int,
        idInstituicao: Int
    ) {
        if (idDoadora <= 0 || idInstituicao <= 0) {
            _mensagemErro.value = "Identificação inválida."
            _estado.value = ConfirmarBancoEstado.ERRO
            return
        }

        chamadaAtual?.cancel()

        _estado.value = ConfirmarBancoEstado.CARREGANDO
        _mensagemErro.value = ""

        val call = MaternaRetrofitFactory().getVinculoEtapaDoadoraService()
            .buscarVinculosDoadora(idDoadora)

        chamadaAtual = call

        call.enqueue(
            object : Callback<VinculoEtapaDoadoraResponse> {

                override fun onResponse(
                    call: Call<VinculoEtapaDoadoraResponse>,
                    response: Response<VinculoEtapaDoadoraResponse>
                ) {
                    if (call !== chamadaAtual) return

                    chamadaAtual = null

                    if (response.code() == 404) {
                        _estado.value = ConfirmarBancoEstado.SEM_VINCULO
                        return
                    }

                    val body = response.body()

                    if (!response.isSuccessful || body?.status != true) {
                        _mensagemErro.value = "Não foi possível verificar seus vínculos."

                        _estado.value = ConfirmarBancoEstado.ERRO
                        return
                    }

                    val jornadas = body.response?.jornadas.orEmpty()

                    Log.d(
                        "ConfirmarBanco",
                        "Jornadas retornadas: ${jornadas.size}"
                    )

                    jornadas.forEach { jornada ->
                        Log.d(
                            "ConfirmarBanco",
                            "Jornada ${jornada.id} | " +
                                    "Instituição ${jornada.instituicao.id} | " +
                                    "Ativa: ${jornada.isAtivo}"
                        )
                    }

                    val jornadasAtivas = jornadas.filter {
                        it.isAtivo == 1
                    }

                    _estado.value = when {
                        jornadasAtivas.isEmpty() -> {
                            ConfirmarBancoEstado.SEM_VINCULO
                        }

                        jornadasAtivas.any {
                            it.instituicao.id != idInstituicao
                        } -> {
                            ConfirmarBancoEstado.OUTRO_BANCO
                        }

                        else -> {
                            ConfirmarBancoEstado.MESMO_BANCO
                        }
                    }

                    Log.i(
                        "ConfirmarBanco",
                        "Jornadas ativas encontradas: ${jornadasAtivas.size}"
                    )
                }

                override fun onFailure(
                    call: Call<VinculoEtapaDoadoraResponse>,
                    t: Throwable
                ) {
                    if (call.isCanceled || call !== chamadaAtual) return

                    chamadaAtual = null
                    _mensagemErro.value = "Não foi possível conectar ao servidor. Tente novamente."
                    _estado.value = ConfirmarBancoEstado.ERRO

                    Log.e(
                        "ConfirmarBanco",
                        "Falha ao consultar vínculos: ${t.message}",
                        t
                    )
                }
            }
        )
    }

    override fun onCleared() {
        chamadaAtual?.cancel()
        super.onCleared()
    }
}