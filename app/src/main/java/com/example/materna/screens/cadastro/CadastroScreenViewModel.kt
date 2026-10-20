package com.example.materna.screens.cadastro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CadastroScreenViewModel : ViewModel() {
    private val _currentStep = MutableLiveData<Int>()
    val currentStep: LiveData<Int> = _currentStep



    private val _nome = MutableLiveData<String>()
    val nome: LiveData<String> = _nome

    private val _dataNascimento = MutableLiveData<String>()
    val dataNascimento: LiveData<String> = _dataNascimento

    private val _cpf = MutableLiveData<String>()
    val cpf: LiveData<String> = _cpf

    private val _telefone = MutableLiveData<String>()
    val telefone: LiveData<String> = _telefone

    private val _cep = MutableLiveData<String>()
    val cep: LiveData<String> = _cep

    private val _estado = MutableLiveData<String>()
    val estado: LiveData<String> = _estado

    private val _cidade = MutableLiveData<String>()
    val cidade: LiveData<String> = _cidade

    private val _endereco = MutableLiveData<String>()
    val endereco: LiveData<String> = _endereco

    private val _numero = MutableLiveData<Int?>()
    val numero: LiveData<Int?> = _numero

    private val _complemento = MutableLiveData<String>()
    val complemento: LiveData<String> = _complemento

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _senha = MutableLiveData<String>()
    val senha: LiveData<String> = _senha

    private val _confirmarSenha = MutableLiveData<String>()
    val confirmarSenha: LiveData<String> = _confirmarSenha

    private val _termosAceitos = MutableLiveData<Boolean>()
    val termosAceitos: LiveData<Boolean> = _termosAceitos

    private val _mostrarSenha = MutableLiveData<Boolean>()
    val mostrarSenha: LiveData<Boolean> = _mostrarSenha

    private val _mostrarConfirmacao = MutableLiveData<Boolean>()
    val mostrarConfirmacao: LiveData<Boolean> = _mostrarConfirmacao





    fun onNomeChanged(value: String) { _nome.value = value }
    fun onDataNascimentoChanged(value: String) { _dataNascimento.value = value }
    fun onCpfChanged(value: String) { _cpf.value = value }
    fun onTelefoneChanged(value: String) { _telefone.value = value }

    fun onCepChanged(value: String) { _cep.value = value }
    fun onEstadoChanged(value: String) { _estado.value = value }
    fun onCidadeChanged(value: String) { _cidade.value = value }
    fun onEnderecoChanged(value: String) { _endereco.value = value }
    fun onNumeroChanged(value: Int?) { _numero.value = value }
    fun onComplementoChanged(value: String) { _complemento.value = value }

    fun onEmailChanged(value: String) { _email.value = value }
    fun onSenhaChanged(value: String) { _senha.value = value }
    fun onConfirmarSenhaChanged(value: String) { _confirmarSenha.value = value }
    fun onTermosAceitosChanged(value: Boolean) { _termosAceitos.value = value }





    fun toggleMostrarSenha() {
        _mostrarSenha.value = !(_mostrarSenha.value ?: false)
    }

    fun toggleMostrarConfirmacao() {
        _mostrarConfirmacao.value = !(_mostrarConfirmacao.value ?: false)
    }

    fun avancarEtapa() {
        val step = _currentStep.value ?: 0
        if (step < 2) _currentStep.value = step + 1
    }

    fun voltarEtapa() {
        val step = _currentStep.value ?: 0
        if (step > 0) _currentStep.value = step - 1
    }





    private val _cepConsultado = MutableLiveData(false)
    val cepConsultado: LiveData<Boolean> = _cepConsultado

    private val _consultandoCep = MutableLiveData(false)
    val consultandoCep: LiveData<Boolean> = _consultandoCep

    private val _erroCep = MutableLiveData<String?>(null)
    val erroCep: LiveData<String?> = _erroCep

    fun podeAvancarEndereco(): Boolean {
        return !(_consultandoCep.value ?: false) &&
                (_cepConsultado.value ?: false) &&
                !(_cep.value.isNullOrBlank()) &&
                !(_estado.value.isNullOrBlank()) &&
                !(_cidade.value.isNullOrBlank()) &&
                !(_endereco.value.isNullOrBlank()) &&
                (_numero.value != null)
    }
}
