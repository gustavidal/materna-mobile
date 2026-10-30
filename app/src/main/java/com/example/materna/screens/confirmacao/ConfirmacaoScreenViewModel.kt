package com.example.materna.screens.confirmacao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.materna.data.local.AppSettingsDao
import com.example.materna.models.requests.ValidarCodigoRequest
import com.example.materna.models.responses.ValidarCodigoResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmacaoScreenViewModel(
    private val settingsDao: AppSettingsDao
) : ViewModel() {

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _codigo = MutableLiveData("")
    val codigo: LiveData<String> = _codigo

    private val _confirmando = MutableLiveData(false)
    val confirmando: LiveData<Boolean> = _confirmando

    private val _mensagemConfirmacao = MutableLiveData<String?>(null)
    val mensagemConfirmacao: LiveData<String?> = _mensagemConfirmacao

    private val _codigoConfirmado = MutableLiveData(false)
    val codigoConfirmado: LiveData<Boolean> = _codigoConfirmado

    private val authService = MaternaRetrofitFactory().getAuthService()

    init {
        carregarEmail()
    }

    private fun carregarEmail() {
        viewModelScope.launch {
            settingsDao.observeSettings().collect { settings ->
                _email.value = settings?.emailPendente.orEmpty()
            }
        }
    }

    fun onCodigoChanged(value: String) {
        _codigo.value = value
            .filter { it in '0'..'9' }
            .take(6)

        _mensagemConfirmacao.value = null
    }

    fun podeConfirmar(): Boolean {
        return _codigo.value.orEmpty().length == 6 &&
                !_email.value.isNullOrBlank() &&
                _confirmando.value != true
    }

    fun confirmarCodigo(idDoadora: Int) {
        if (_confirmando.value == true) return

        val codigoDigitado = _codigo.value.orEmpty()

        if (idDoadora <= 0) {
            _mensagemConfirmacao.value = "Identificação da doadora inválida."
            return
        }

        if (codigoDigitado.length != 6 || !codigoDigitado.all { it in '0'..'9' }) {
            _mensagemConfirmacao.value = "Informe os seis dígitos do código."
            return
        }

        if (_email.value.isNullOrBlank()) {
            _mensagemConfirmacao.value = "Não foi possível recuperar o e-mail cadastrado."
            return
        }

        val request = ValidarCodigoRequest(
            idDoadora = idDoadora,
            codigo = codigoDigitado
        )

        _confirmando.value = true
        _mensagemConfirmacao.value = null
        _codigoConfirmado.value = false

        val call = authService.validarCodigo(request)

        call.enqueue(object : Callback<ValidarCodigoResponse> {
            override fun onResponse(
                call: Call<ValidarCodigoResponse>,
                response: Response<ValidarCodigoResponse>
            ) {
                Log.i("ConfirmarCodigo", "Response ${response.body()}")

                if (response.isSuccessful) {
                    val resultado = response.body()

                    if (resultado?.status == true) {
                        viewModelScope.launch {
                            try {
                                settingsDao.limparEmailPendente()

                                _mensagemConfirmacao.value =
                                    resultado.message.ifBlank {
                                        "E-mail confirmado com sucesso!"
                                    }

                                _codigoConfirmado.value = true
                            } catch (e: Exception) {
                                Log.e("ConfirmarCodigo", "Erro ao atualizar dados locais", e)

                                _mensagemConfirmacao.value = "Código confirmado, mas houve um erro ao atualizar os dados locais."
                            } finally {
                                _confirmando.value = false
                            }
                        }
                    } else {
                        _confirmando.value = false

                        _mensagemConfirmacao.value =
                            resultado?.message?.takeIf {
                                it.isNotBlank()
                            } ?: "Código inválido ou expirado."
                    }
                } else {
                    _confirmando.value = false

                    val mensagemErro = try {
                        val erro = response.errorBody()?.string()

                        if (!erro.isNullOrBlank()) {
                            Gson().fromJson(
                                erro,
                                ValidarCodigoResponse::class.java
                            )?.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("ConfirmarCodigo", "Erro ao interpretar resposta", e)
                        null
                    }

                    _mensagemConfirmacao.value =
                        mensagemErro?.takeIf {
                            it.isNotBlank()
                        } ?: "Não foi possível confirmar o código. HTTP ${response.code()}."
                }
            }

            override fun onFailure(
                call: Call<ValidarCodigoResponse>,
                t: Throwable
            ) {
                _confirmando.value = false

                _mensagemConfirmacao.value = "Não foi possível conectar à API. Tente novamente."

                Log.e("ConfirmarCodigo", "Falha ${t.message}", t)
            }
        })
    }
}