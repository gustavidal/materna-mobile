package com.example.materna.screens.agendamento

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materna.services.materna.MaternaRetrofitFactory
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AgendamentoScreenViewModel : ViewModel() {

    private val _tipoAtendimento = MutableLiveData<String>()
    val tipoAtendimento: LiveData<String> = _tipoAtendimento

    private val _dataSelecionada = MutableLiveData<String?>()
    val dataSelecionada: LiveData<String?> = _dataSelecionada

    private val _horarioSelecionado = MutableLiveData<Int?>()
    val horarioSelecionado: LiveData<Int?> = _horarioSelecionado

    private val _isFormularioValido = MutableLiveData(false)
    val isFormularioValido: LiveData<Boolean> = _isFormularioValido

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _mensagemErro = MutableLiveData<String?>()
    val mensagemErro: LiveData<String?> = _mensagemErro

    fun onTipoAtendimentoChanged(tipo: String) {
        if (_tipoAtendimento.value == tipo) return

        _tipoAtendimento.value = tipo
        _dataSelecionada.value = null
        _horarioSelecionado.value = null

        validarFormulario()
    }

    fun onDataChanged(data: String) {
        if (_dataSelecionada.value == data) return

        _dataSelecionada.value = data
        _horarioSelecionado.value = null

        validarFormulario()
    }

    fun onHorarioChanged(idHorario: Int) {
        _horarioSelecionado.value = idHorario

        validarFormulario()
    }

    private fun validarFormulario() {
        _isFormularioValido.value =
            !_tipoAtendimento.value.isNullOrBlank() &&
                    !_dataSelecionada.value.isNullOrBlank() &&
                    _horarioSelecionado.value != null
    }


    fun testarBuscaHorarios(idInstituicao: Int, context: Context) {
        Log.d(
            "AGENDAMENTO_API",
            "Buscando horários da instituição: $idInstituicao"
        )

        MaternaRetrofitFactory(context)
            .getHorarioService()
            .buscarHorariosDisponiveis(
                idInstituicao = idInstituicao
            )
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d(
                        "AGENDAMENTO_API",
                        "HTTP: ${response.code()}"
                    )

                    Log.d("AGENDAMENTO_API", "URL: ${call.request().url}")

                    val json = if (response.isSuccessful) {
                        response.body()?.string()
                    } else {
                        response.errorBody()?.string()
                    }

                    json.orEmpty()
                        .chunked(3000)
                        .forEachIndexed { index, parte ->
                            Log.d(
                                "AGENDAMENTO_API",
                                "JSON [$index]: $parte"
                            )
                        }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {
                    Log.e(
                        "AGENDAMENTO_API",
                        "Falha ao buscar horários",
                        t
                    )
                }
            })
    }

    fun limparMensagemErro() {
        _mensagemErro.value = null
    }
}