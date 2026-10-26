package com.example.materna.screens.cadastro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materna.model.CadastroDoadora
import com.example.materna.model.CadastroDoadoraResponse
import com.example.materna.model.Endereco
import com.example.materna.service.MaternaRetrofitFactory
import com.example.materna.service.RetrofitFactory
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CadastroScreenViewModel: ViewModel() {
    private val doadoraService = MaternaRetrofitFactory().getDoadoraService()

    private val _cadastrando = MutableLiveData(false)
    val cadastrando: LiveData<Boolean> = _cadastrando

    private val _mensagemCadastro = MutableLiveData<String?>(null)
    val mensagemCadastro: LiveData<String?> = _mensagemCadastro

    private val _cadastroConcluido = MutableLiveData(false)
    val cadastroConcluido: LiveData<Boolean> = _cadastroConcluido





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

    private val _logradouro = MutableLiveData<String>()
    val logradouro: LiveData<String> = _logradouro

    private val _bairro = MutableLiveData<String>()
    val bairro: LiveData<String> = _bairro

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
    fun onDataNascimentoChanged(value: String) {
        _dataNascimento.value = formatarData(value)
    }
    fun onCpfChanged(value: String) {
        _cpf.value = formatarCpf(value)
    }
    fun onTelefoneChanged(value: String) {
        _telefone.value = formatarTelefone(value)
    }


    private val enderecoService = RetrofitFactory().getEnderecoService()
    private var chamadaCep: Call<Endereco>? = null


    fun onCepChanged(value: String) {
        val numeros = value.filter { it.isDigit() }.take(8)
        val cepFormatado = formatarCep(numeros)

        if (cepFormatado == _cep.value) return

        chamadaCep?.cancel()
        chamadaCep = null

        _cep.value = cepFormatado
        _cepConsultado.value = false
        _consultandoCep.value = false
        _erroCep.value = null

        _estado.value = ""
        _cidade.value = ""
        _logradouro.value = ""
        _bairro.value = ""

        if (numeros.length == 8) {
            consultarCep(numeros)
        }
    }
    fun onEstadoChanged(value: String) { _estado.value = value }
    fun onCidadeChanged(value: String) { _cidade.value = value }
    fun onLogradouroChanged(value: String) { _logradouro.value = value }
    fun onBairroChanged(value: String) { _bairro.value = value }
    fun onNumeroChanged(value: Int?) { _numero.value = value }
    fun onComplementoChanged(value: String) { _complemento.value = value }

    fun onEmailChanged(value: String) { _email.value = value }
    fun onSenhaChanged(value: String) { _senha.value = value }
    fun onConfirmarSenhaChanged(value: String) { _confirmarSenha.value = value }
    fun onTermosAceitosChanged(value: Boolean) { _termosAceitos.value = value }






    private fun formatarData(value: String): String {
        val digits = value.filter { it.isDigit() }.take(8)

        return buildString {
            digits.forEachIndexed { index, char ->
                if (index == 2 || index == 4) append('/')
                append(char)
            }
        }
    }

    private fun formatarCpf(value: String): String {
        val digits = value.filter { it.isDigit() }.take(11)

        return buildString {
            digits.forEachIndexed { index, char ->
                if (index == 3 || index == 6) append('.')
                if (index == 9) append('-')
                append(char)
            }
        }
    }

    private fun formatarTelefone(value: String): String {
        val digits = value.filter { it.isDigit() }.take(11)

        return buildString {
            digits.forEachIndexed { index, char ->
                when (index) {
                    0 -> append('(')
                    2 -> append(") ")
                    7 -> append('-')
                }
                append(char)
            }
        }
    }

    private fun formatarCep(value: String): String {
        val digits = value.filter { it.isDigit() }.take(8)

        return buildString {
            digits.forEachIndexed { index, char ->
                if (index == 5) append('-')
                append(char)
            }
        }
    }






    private fun dataNascimentoValida(): Boolean {
        val valor = _dataNascimento.value ?: return false

        if (!valor.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
            return false
        }

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formato.isLenient = false

        val dataNascimento = try {
            formato.parse(valor)
        } catch (e: Exception) {
            null
        } ?: return false

        val hoje = Calendar.getInstance()
        val nascimento = Calendar.getInstance().apply {
            time = dataNascimento
        }

        return !nascimento.after(hoje)
    }

    private fun cpfValido(): Boolean {
        val numeros = _cpf.value
            ?.filter { it.isDigit() }
            ?: return false

        if (numeros.length != 11) return false

        // Rejeita CPFs com todos os dígitos iguais.
        if (numeros.all { it == numeros[0] }) return false

        val digitos = numeros.map { it.digitToInt() }

        // Calcula o primeiro dígito verificador.
        var soma = 0

        for (i in 0 until 9) {
            soma += digitos[i] * (10 - i)
        }

        val restoPrimeiro = (soma * 10) % 11
        val primeiroDigito = if (restoPrimeiro == 10) {
            0
        } else {
            restoPrimeiro
        }

        if (digitos[9] != primeiroDigito) return false

        // Calcula o segundo dígito verificador.
        soma = 0

        for (i in 0 until 10) {
            soma += digitos[i] * (11 - i)
        }

        val restoSegundo = (soma * 10) % 11
        val segundoDigito = if (restoSegundo == 10) {
            0
        } else {
            restoSegundo
        }

        return digitos[10] == segundoDigito
    }

    fun emailValido(): Boolean {
        val email = _email.value.orEmpty()

        return android.util.Patterns.EMAIL_ADDRESS
            .matcher(email)
            .matches()
    }





    fun toggleMostrarSenha() {
        _mostrarSenha.value = !(_mostrarSenha.value ?: false)
    }

    fun toggleMostrarConfirmacao() {
        _mostrarConfirmacao.value = !(_mostrarConfirmacao.value ?: false)
    }

    fun ocultarSenha() {
        _mostrarSenha.value = false
    }

    fun ocultarConfirmacao() {
        _mostrarConfirmacao.value = false
    }

    fun avancarEtapa() {
        val step = _currentStep.value ?: 0
        if (step < 2) _currentStep.value = step + 1
    }

    fun voltarEtapa() {
        val step = _currentStep.value ?: 0
        if (step > 0) _currentStep.value = step - 1
    }



    fun erroDataNascimento(): String? {
        val valor = _dataNascimento.value.orEmpty()

        if (valor.isBlank()) return null

        if (valor.length < 10) {
            return "Digite a data completa."
        }

        return if (!dataNascimentoValida()) "Informe uma data válida." else null
    }

    fun erroCpf(): String? {
        val valor = _cpf.value.orEmpty()

        if (valor.isBlank()) return null

        if (valor.filter { it.isDigit() }.length < 11) {
            return "Digite os 11 dígitos do CPF."
        }

        return if (!cpfValido()) "CPF inválido. Confira os números informados." else null
    }






    private fun consultarCep(cep: String) {
        _consultandoCep.value = true
        _erroCep.value = null

        val chamada = enderecoService.getEnderecoByCep(cep)
        chamadaCep = chamada

        chamada.enqueue(object : Callback<Endereco> {

            override fun onResponse(
                call: Call<Endereco>,
                response: Response<Endereco>
            ) {
                if (_cep.value?.filter { it.isDigit() } != cep) return

                _consultandoCep.value = false

                val enderecoApi = response.body()

                if (
                    response.isSuccessful &&
                    enderecoApi != null &&
                    enderecoApi.erro != true &&
                    !enderecoApi.cep.isNullOrBlank() &&
                    !enderecoApi.uf.isNullOrBlank() &&
                    !enderecoApi.cidade.isNullOrBlank()
                ) {
                    _estado.value = enderecoApi.uf
                    _cidade.value = enderecoApi.cidade
                    _logradouro.value = enderecoApi.logradouro
                    _bairro.value = enderecoApi.bairro
                    _cepConsultado.value = true
                    _erroCep.value = null
                } else {
                    _estado.value = ""
                    _cidade.value = ""
                    _logradouro.value = ""
                    _cepConsultado.value = false
                    _erroCep.value = "CEP não encontrado. Confira os números."
                }

            }

            override fun onFailure(
                call: Call<Endereco>,
                t: Throwable
            ) {
                if (_cep.value?.filter { it.isDigit() } != cep) return

                _consultandoCep.value = false
                _cepConsultado.value = false

                android.util.Log.e("ViaCEP", "Erro na consulta do CEP", t)

                if (!call.isCanceled) {
                    _erroCep.value =
                        "Não foi possível consultar o CEP. Verifique sua conexão."
                }
            }
        })
    }

    private val _cepConsultado = MutableLiveData(false)
    val cepConsultado: LiveData<Boolean> = _cepConsultado

    private val _consultandoCep = MutableLiveData(false)
    val consultandoCep: LiveData<Boolean> = _consultandoCep

    private val _erroCep = MutableLiveData<String?>(null)
    val erroCep: LiveData<String?> = _erroCep





    fun senhaTemOitoCaracteres(): Boolean {
        return (_senha.value?.length ?: 0) >= 8
    }

    fun senhaTemMaiuscula(): Boolean {
        return _senha.value.orEmpty().any { it.isUpperCase() }
    }

    fun senhaTemNumero(): Boolean {
        return _senha.value.orEmpty().any { it.isDigit() }
    }

    fun senhaTemCaractereEspecial(): Boolean {
        return _senha.value.orEmpty().any {
            !it.isLetterOrDigit() && !it.isWhitespace()
        }
    }





    fun podeContinuar(): Boolean {
        return when (_currentStep.value ?: 0) {
            0 -> {
                !_nome.value.isNullOrBlank() &&
                        dataNascimentoValida() &&
                        cpfValido() &&
                        !_telefone.value.isNullOrBlank()
            }

            1 -> {
                !(_consultandoCep.value ?: false) &&
                        (_cepConsultado.value ?: false) &&
                        !_cep.value.isNullOrBlank() &&
                        !_estado.value.isNullOrBlank() &&
                        !_cidade.value.isNullOrBlank() &&
                        !_logradouro.value.isNullOrBlank() &&
                        _numero.value != null
            }

            2 -> {
                emailValido() &&
                        senhaTemOitoCaracteres() &&
                        senhaTemMaiuscula() &&
                        senhaTemNumero() &&
                        senhaTemCaractereEspecial() &&
                        !_confirmarSenha.value.isNullOrBlank() &&
                        _senha.value == _confirmarSenha.value &&
                        (_termosAceitos.value ?: false)
            }

            else -> false
        }
    }





    fun cadastrarDoadora() {
        if (_cadastrando.value == true) return

        val dataOriginal = _dataNascimento.value.orEmpty()
        val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            isLenient = false
        }

        val dataConvertida = try {
            formatoEntrada.parse(dataOriginal)?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(it)
            }
        } catch (e: Exception) {
            null
        }

        if (dataConvertida == null) {
            _mensagemCadastro.value = "Informe uma data de nascimento válida."
            return
        }

        val numero = _numero.value
        if (numero == null || numero <= 0) {
            _mensagemCadastro.value = "Informe um número de endereço válido."
            return
        }

        val doadora = CadastroDoadora(
            nome = _nome.value.orEmpty(),
            cpf = _cpf.value.orEmpty(),
            dataNascimento = dataConvertida,
            email = _email.value.orEmpty(),
            senha = _senha.value.orEmpty(),
            telefone = _telefone.value.orEmpty().filter { it.isDigit() },
            cep = _cep.value.orEmpty(),
            siglaEstado = _estado.value.orEmpty(),
            cidade = _cidade.value.orEmpty(),
            logradouro = _logradouro.value.orEmpty(),
            bairro = _bairro.value.orEmpty(),
            numero = numero,
            complemento = _complemento.value.orEmpty().ifBlank { null }
        )

        _cadastrando.value = true
        _mensagemCadastro.value = null
        _cadastroConcluido.value = false

        doadoraService.cadastrarDoadora(doadora)
            .enqueue(object : Callback<CadastroDoadoraResponse> {

                override fun onResponse(
                    call: Call<CadastroDoadoraResponse>,
                    response: Response<CadastroDoadoraResponse>
                ) {
                    _cadastrando.value = false

                    if (response.isSuccessful) {
                        val resultado = response.body()

                        if (resultado?.status == true) {
                            _cadastroConcluido.value = true
                            _mensagemCadastro.value =
                                resultado.message.ifBlank { "Cadastro realizado com sucesso!" }
                        } else {
                            _mensagemCadastro.value =
                                resultado?.message ?: "Não foi possível concluir o cadastro."
                        }
                    } else {
                        val erro = try {
                            response.errorBody()?.string()?.let {
                                Gson().fromJson(
                                    it,
                                    CadastroDoadoraResponse::class.java
                                )
                            }
                        } catch (e: Exception) {
                            null
                        }

                        _mensagemCadastro.value = when {
                            !erro?.field.isNullOrBlank() ->
                                "${erro?.message.orEmpty()} ${erro?.field.orEmpty()}"

                            !erro?.message.isNullOrBlank() ->
                                erro?.message

                            else ->
                                "Erro ao cadastrar (HTTP ${response.code()})."
                        }
                    }
                }

                override fun onFailure(
                    call: Call<CadastroDoadoraResponse>,
                    t: Throwable
                ) {
                    _cadastrando.value = false
                    _mensagemCadastro.value =
                        "Não foi possível conectar à API. Verifique se o backend está ativo."
                    android.util.Log.e("CadastroDoadora", "Erro ao cadastrar doadora", t)
                }
            })
    }




    fun preencherEnderecoDeTeste() {
        _cep.value = "06602-330"
        _estado.value = "SP"
        _cidade.value = "São Paulo"
        _logradouro.value = "Rua João Fiochi"
        _bairro.value = "Jardim Lindomar"
        _numero.value = 67
        _complemento.value = "Casa 2"

        _cepConsultado.value = true
        _consultandoCep.value = false
        _erroCep.value = null
    }

}