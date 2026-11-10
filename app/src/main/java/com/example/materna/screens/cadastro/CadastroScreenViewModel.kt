package com.example.materna.screens.cadastro

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.materna.data.local.AppSettingsDao
import com.example.materna.models.requests.CadastroDoadora
import com.example.materna.models.responses.CadastroDoadoraResponse
import com.example.materna.models.responses.Endereco
import com.example.materna.services.materna.MaternaRetrofitFactory
import com.example.materna.services.viacep.RetrofitFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CadastroScreenViewModel(
    private val settingsDao: AppSettingsDao
) : ViewModel() {
    private val _cadastrando = MutableLiveData<Boolean>()
    val cadastrando: LiveData<Boolean> = _cadastrando

    private val _mensagemCadastro = MutableLiveData<String?>()
    val mensagemCadastro: LiveData<String?> = _mensagemCadastro

    private val _cadastroConcluido = MutableLiveData<Boolean>()
    val cadastroConcluido: LiveData<Boolean> = _cadastroConcluido

    private val _idDoadora = MutableLiveData<Int?>()
    val idDoadora: LiveData<Int?> = _idDoadora





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

    private val _complemento = MutableLiveData<String?>()
    val complemento: LiveData<String?> = _complemento

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





    fun onNomeChanged(value: String) { _nome.value = value.take(100) }
    fun onDataNascimentoChanged(value: String) {
        _dataNascimento.value = formatarData(value)
    }
    fun onCpfChanged(value: String) {
        _cpf.value = formatarCpf(value)
    }
    fun onTelefoneChanged(value: String) {
        _telefone.value = formatarTelefone(value)
    }



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
    fun onComplementoChanged(value: String?) { _complemento.value = value }

    fun onEmailChanged(value: String) {
        _email.value = value
    }
    fun onSenhaChanged(value: String) { _senha.value = value }
    fun onConfirmarSenhaChanged(value: String) {
        _confirmarSenha.value = value
    }
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
                when {
                    index == 0 -> append('(')
                    index == 2 -> append(") ")
                    index == 6 && digits.length <= 10 -> append('-')
                    index == 7 && digits.length == 11 -> append('-')
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
    fun telefoneValido(): Boolean {
        val numeros = _telefone.value.orEmpty()
            .filter { it.isDigit() }

        return numeros.length == 10 || numeros.length == 11
    }
    fun emailValido(): Boolean {
        val valor = _email.value.orEmpty()
        val usuario = valor.substringBefore("@")

        return android.util.Patterns.EMAIL_ADDRESS
            .matcher(valor)
            .matches() &&
                usuario.length in 6..256 &&
                !usuario.all { it.isDigit() }
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





    fun erroNome(): String? {
        val valor = _nome.value.orEmpty().trim()

        return when {
            valor.isEmpty() -> "Informe seu nome completo."
            else -> null
        }
    }
    fun erroDataNascimento(): String? {
        val valor = _dataNascimento.value.orEmpty()

        return when {
            valor.isEmpty() -> "Informe sua data de nascimento."
            valor.length < 10 -> "Complete a data de nascimento."
            !dataNascimentoValida() -> "Informe uma data válida."
            else -> null
        }
    }
    fun erroCpf(): String? {
        val numeros = _cpf.value.orEmpty().filter { it.isDigit() }

        return when {
            numeros.isEmpty() -> "Informe seu CPF."
            numeros.length < 11 -> "Complete os 11 dígitos do CPF."
            !cpfValido() -> "CPF inválido. Confira os números informados."
            else -> null
        }
    }
    fun erroTelefone(): String? {
        val numeros = _telefone.value.orEmpty().filter { it.isDigit() }

        return when {
            numeros.isEmpty() -> "Informe seu telefone."
            !telefoneValido() ->
                "Informe um telefone com DDD e 8 ou 9 dígitos."
            else -> null
        }
    }
    fun erroCepPreenchimento(): String? {
        val numeros = _cep.value.orEmpty().filter { it.isDigit() }

        return when {
            numeros.isEmpty() -> "Informe seu CEP."
            numeros.length < 8 -> "Complete os 8 dígitos do CEP."
            else -> null
        }
    }
    fun erroNumero(): String? {
        return when {
            _numero.value == null -> "Informe o número do endereço."
            _numero.value!! <= 0 -> "Informe um número maior que zero."
            else -> null
        }
    }
    fun erroEmail(): String? {
        val valor = _email.value.orEmpty()

        return when {
            valor.isBlank() -> "Informe seu e-mail."
            !emailValido() -> "E-mail inválido. Verifique o endereço informado."
            else -> null
        }
    }
    fun erroConfirmarSenha(): String? {
        val confirmacao = _confirmarSenha.value.orEmpty()

        return when {
            confirmacao.isEmpty() -> "Confirme sua senha."
            confirmacao != _senha.value -> "As senhas não coincidem."
            else -> null
        }
    }
    fun senhasCoincidem(): Boolean {
        return !_confirmarSenha.value.isNullOrEmpty() &&
                _confirmarSenha.value == _senha.value
    }





    private fun consultarCep(cep: String) {
        _consultandoCep.value = true
        _erroCep.value = null

        val call = RetrofitFactory().getEnderecoService()
            .getEnderecoByCep(cep)

        chamadaCep = call

        call.enqueue(object : Callback<Endereco> {
            override fun onResponse(
                call: Call<Endereco>,
                response: Response<Endereco>
            ) {
                if (_cep.value?.filter { it.isDigit() } != cep) return

                _consultandoCep.value = false

                val endereco = response.body()

                if (
                    response.isSuccessful &&
                    endereco != null &&
                    endereco.erro != true &&
                    !endereco.cep.isNullOrBlank() &&
                    !endereco.uf.isNullOrBlank() &&
                    !endereco.cidade.isNullOrBlank()
                ) {
                    _estado.value = endereco.uf
                    _cidade.value = endereco.cidade
                    _logradouro.value = endereco.logradouro
                    _bairro.value = endereco.bairro

                    _cepConsultado.value = true
                    _erroCep.value = null

                    Log.i("ViaCEP", "Endereço encontrado: $endereco")
                } else {
                    limparEndereco()

                    _erroCep.value = "CEP não encontrado. Confira os números."

                    Log.i("ViaCEP", "CEP não encontrado: $cep")
                }
            }

            override fun onFailure(
                call: Call<Endereco>,
                t: Throwable
            ) {
                if (call.isCanceled) return
                if (_cep.value?.filter { it.isDigit() } != cep) return

                _consultandoCep.value = false
                limparEndereco()

                _erroCep.value = "Não foi possível consultar o CEP. Verifique sua conexão."

                Log.e("ViaCEP", "Falha: ${t.message}", t)
            }
        })
    }

    private fun limparEndereco() {
        _estado.value = ""
        _cidade.value = ""
        _logradouro.value = ""
        _bairro.value = ""
        _cepConsultado.value = false
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





    private val _camposVisitados = MutableLiveData<Set<String>>(emptySet())
    val camposVisitados: LiveData<Set<String>> = _camposVisitados

    fun onCampoPerdeuFoco(campo: String) {
        _camposVisitados.value = _camposVisitados.value.orEmpty() + campo
    }





    fun podeContinuar(): Boolean {
        return when (_currentStep.value ?: 0) {
            0 -> {
                !_nome.value.isNullOrBlank() &&
                        dataNascimentoValida() &&
                        cpfValido() &&
                        telefoneValido()
            }
            1 -> {
                !(_consultandoCep.value ?: false) &&
                        (_cepConsultado.value ?: false) &&
                        !_cep.value.isNullOrBlank() &&
                        !_estado.value.isNullOrBlank() &&
                        !_cidade.value.isNullOrBlank() &&
                        !_logradouro.value.isNullOrBlank() &&
                        (_numero.value ?: 0) > 0
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

        val formatoEntrada = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).apply {
            isLenient = false
        }

        val dataConvertida = try {
            formatoEntrada.parse(dataOriginal)?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(it)
            }?.trim()
        } catch (e: Exception) {
            null
        }

        if (dataConvertida == null) {
            _mensagemCadastro.value = "Informe uma data de nascimento válida."
            return
        }

        if (_numero.value == null || _numero.value!! <= 0) {
            _mensagemCadastro.value = "Informe um número de endereço válido."
            return
        }

        if (_complemento.value == "") {
            onComplementoChanged(null)
        }

        val doadora = CadastroDoadora(
            nome = _nome.value.orEmpty().trim(),
            cpf = _cpf.value.orEmpty().trim(),
            dataNascimento = dataConvertida,
            email = _email.value.orEmpty().trim(),
            senha = _senha.value.orEmpty(),
            telefone = _telefone.value.orEmpty().filter { it.isDigit() }.trim(),
            cep = _cep.value.orEmpty().trim(),
            siglaEstado = _estado.value.orEmpty().trim(),
            cidade = _cidade.value.orEmpty().trim(),
            logradouro = _logradouro.value.orEmpty().trim(),
            bairro = _bairro.value.orEmpty().trim(),
            numero = _numero.value!!,
            complemento = _complemento.value?.trim()
        )

        _cadastrando.value = true
        _mensagemCadastro.value = null
        _cadastroConcluido.value = false

        val call = MaternaRetrofitFactory().getDoadoraService()
            .cadastrarDoadora(doadora)

        call.enqueue(object : Callback<CadastroDoadoraResponse> {
            override fun onResponse(
                call: Call<CadastroDoadoraResponse>,
                response: Response<CadastroDoadoraResponse>
            ) {
                Log.i("CadastroDoadora", "Response: ${response.body()}")

                if (response.isSuccessful) {
                    val resultado = response.body()

                    if (resultado?.status == true) {
                        val id = resultado.response?.id

                        if (id == null) {
                            _mensagemCadastro.value = "Cadastro realizado, mas a API não retornou o ID da doadora."
                            _cadastrando.value = false
                            return
                        }

                        viewModelScope.launch {
                            try {
                                settingsDao.salvarCadastroPendente(
                                    idDoadora = id,
                                    email = doadora.email
                                )

                                _idDoadora.value = id
                                _cadastroConcluido.value = true
                            } catch (e: Exception) {
                                Log.e("CadastroDoadora", "Erro ao salvar e-mail", e)
                                _mensagemCadastro.value = "Cadastro realizado, mas não foi possível salvar o e-mail."
                            } finally {
                                _cadastrando.value = false
                            }
                        }
                    } else {
                        _cadastrando.value = false
                        _mensagemCadastro.value = resultado?.message ?: "Não foi possível concluir o cadastro."
                    }
                } else {
                    _cadastrando.value = false

                    val erro = try {
                        response.errorBody()?.string()?.let {
                            Gson().fromJson(it, CadastroDoadoraResponse::class.java)
                        }
                    } catch (e: Exception) {
                        null
                    }

                    _mensagemCadastro.value = when {
                        !erro?.field.isNullOrBlank() -> "${erro?.message.orEmpty()} ${erro?.field.orEmpty()}"
                        !erro?.message.isNullOrBlank() -> erro?.message
                        else -> "Erro ao cadastrar (HTTP ${response.code()})."
                    }

                    Log.e("CadastroDoadora", "Erro HTTP ${response.code()}: ${_mensagemCadastro.value}")
                }
            }

            override fun onFailure(
                call: Call<CadastroDoadoraResponse>,
                t: Throwable
            ) {
                _cadastrando.value = false

                if (call.isCanceled) return

                _mensagemCadastro.value =
                    "Não foi possível conectar à API. Verifique se o backend está ativo."

                Log.e(
                    "CadastroDoadora",
                    "Falha: ${t.message}",
                    t
                )
            }
        })
    }
}