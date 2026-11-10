package com.example.materna.screens.perfil

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materna.data.local.AppSettingsDao
import com.example.materna.models.requests.AtualizarDoadoraRequest
import com.example.materna.models.responses.AtualizarDoadoraResponse
import com.example.materna.models.responses.Endereco
import com.example.materna.models.responses.PerfilDoadora
import com.example.materna.models.responses.PerfilDoadoraResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import com.example.materna.services.viacep.RetrofitFactory
import com.example.materna.utils.ErroCampos
import com.example.materna.utils.FormatadorCampos
import com.example.materna.utils.ValidadorCampos
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarPerfilScreenViewModel(
    private val settingsDao: AppSettingsDao
) : ViewModel() {
    private val _idDoadora = MutableLiveData<Int?>(null)
    val idDoadora: LiveData<Int?> = _idDoadora

    private val _nome = MutableLiveData<String>()
    val nome: LiveData<String> = _nome

    private val _cpf = MutableLiveData<String>()
    val cpf: LiveData<String> = _cpf

    private val _dataNascimento = MutableLiveData<String>()
    val dataNascimento: LiveData<String> = _dataNascimento

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _telefone = MutableLiveData<String>()
    val telefone: LiveData<String> = _telefone

    private val _cep = MutableLiveData<String>()
    val cep: LiveData<String> = _cep

    private val _siglaEstado = MutableLiveData<String>()
    val siglaEstado: LiveData<String> = _siglaEstado

    private val _cidade = MutableLiveData<String>()
    val cidade: LiveData<String> = _cidade

    private val _bairro = MutableLiveData<String>()
    val bairro: LiveData<String> = _bairro

    private val _logradouro = MutableLiveData<String>()
    val logradouro: LiveData<String> = _logradouro

    private val _numero = MutableLiveData<String>()
    val numero: LiveData<String> = _numero

    private val _complemento = MutableLiveData<String>()
    val complemento: LiveData<String> = _complemento

    private val _senha = MutableLiveData<String>()
    val senha: LiveData<String> = _senha

    private val _confirmarSenha = MutableLiveData<String>()
    val confirmarSenha: LiveData<String> = _confirmarSenha

    private val _mostrarSenha = MutableLiveData<Boolean>()
    val mostrarSenha: LiveData<Boolean> = _mostrarSenha

    private val _mostrarConfirmacao = MutableLiveData<Boolean>()
    val mostrarConfirmacao: LiveData<Boolean> = _mostrarConfirmacao

    private val _carregando = MutableLiveData<Boolean>()
    val carregando: LiveData<Boolean> = _carregando

    private val _perfilCarregado = MutableLiveData<Boolean>()
    val perfilCarregado: LiveData<Boolean> = _perfilCarregado

    private val _salvando = MutableLiveData<Boolean>()
    val salvando: LiveData<Boolean> = _salvando

    private val _mensagemErro = MutableLiveData<String?>()
    val mensagemErro: LiveData<String?> = _mensagemErro

    private val _atualizacaoConcluida = MutableLiveData<Boolean>()
    val atualizacaoConcluida: LiveData<Boolean> = _atualizacaoConcluida

    private var chamadaAtualizacao: Call<AtualizarDoadoraResponse>? = null

    private var chamadaAtual: Call<PerfilDoadoraResponse>? = null

    private var chamadaCep: Call<Endereco>? = null

    private val _consultandoCep = MutableLiveData<Boolean>()
    val consultandoCep: LiveData<Boolean> = _consultandoCep

    private val _cepConsultado = MutableLiveData<Boolean>()
    val cepConsultado: LiveData<Boolean> = _cepConsultado

    private val _erroCep = MutableLiveData<String?>()
    val erroCep: LiveData<String?> = _erroCep

    private val _camposVisitados = MutableLiveData<Set<String>>()
    val camposVisitados: LiveData<Set<String>> = _camposVisitados

    fun onCampoPerdeuFoco(campo: String) {
        _camposVisitados.value =
            _camposVisitados.value.orEmpty() + campo
    }





    fun erroNome(): String? {
        return ErroCampos.nome(_nome.value.orEmpty())
    }

    fun erroDataNascimento(): String? {
        return ErroCampos.dataNascimento(
            _dataNascimento.value.orEmpty()
        )
    }

    fun erroCpf(): String? {
        return ErroCampos.cpf(_cpf.value.orEmpty())
    }

    fun erroTelefone(): String? {
        return ErroCampos.telefone(_telefone.value.orEmpty())
    }

    fun erroEmail(): String? {
        return ErroCampos.email(_email.value.orEmpty())
    }

    fun senhaTemOitoCaracteres(): Boolean {
        return ValidadorCampos.senhaTemOitoCaracteres(
            _senha.value.orEmpty()
        )
    }

    fun senhaTemMaiuscula(): Boolean {
        return ValidadorCampos.senhaTemMaiuscula(
            _senha.value.orEmpty()
        )
    }

    fun senhaTemNumero(): Boolean {
        return ValidadorCampos.senhaTemNumero(
            _senha.value.orEmpty()
        )
    }

    fun senhaTemCaractereEspecial(): Boolean {
        return ValidadorCampos.senhaTemCaractereEspecial(
            _senha.value.orEmpty()
        )
    }

    fun erroConfirmarSenha(): String? {
        return ErroCampos.confirmarSenha(
            _senha.value.orEmpty(),
            _confirmarSenha.value.orEmpty()
        )
    }

    fun erroCepPreenchimento(): String? {
        return ErroCampos.cep(_cep.value.orEmpty())
    }

    fun erroNumero(): String? {
        return ErroCampos.numero(_numero.value.orEmpty())
    }





    init {
        carregarPerfil()
    }

    private fun carregarPerfil() {
        val idTeste = 11

        _idDoadora.value = idTeste
        buscarDoadora(idTeste)
    }

    fun recarregarPerfil() {
        val id = _idDoadora.value

        if (id != null && id > 0) {
            buscarDoadora(id)
        } else {
            carregarPerfil()
        }
    }

//    private fun carregarPerfil() {
//        viewModelScope.launch {
//            try {
//                val settings = settingsDao.observeSettings().first()
//                val id = settings?.idDoadora
//
//                if (id != null && id > 0) {
//                    _idDoadora.value = id
//                    buscarDoadora(id)
//                } else {
//                    _mensagemErro.value = "Nenhuma doadora identificada."
//                }
//            } catch (e: Exception) {
//                Log.e("EditarPerfil", "Erro ao recuperar ID", e)
//                _mensagemErro.value = "Não foi possível identificar a doadora."
//            }
//        }
//    }

    private fun buscarDoadora(id: Int) {
        chamadaAtual?.cancel()

        _carregando.value = true
        _mensagemErro.value = null
        _perfilCarregado.value = false

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

                val resultado = response.body()

                if (response.isSuccessful && resultado?.status == true) {
                    val doadora = resultado.response
                        ?.doadora
                        ?.firstOrNull { it.id == id }

                    if (doadora != null) {
                        preencherCampos(doadora)
                        _perfilCarregado.value = true
                    } else {
                        _mensagemErro.value = "Doadora não encontrada."
                    }
                } else {
                    _mensagemErro.value = resultado?.message ?: "Erro ao carregar dados (HTTP ${response.code()})."
                }
            }

            override fun onFailure(
                call: Call<PerfilDoadoraResponse>,
                t: Throwable
            ) {
                if (call.isCanceled || call !== chamadaAtual) return

                _carregando.value = false
                _mensagemErro.value =
                    "Não foi possível conectar à API."

                Log.e("EditarPerfil", "Falha ao carregar perfil", t)
            }
        })
    }



    private fun preencherCampos(doadora: PerfilDoadora) {
        _nome.value = doadora.nome
        _cpf.value = FormatadorCampos.cpf(doadora.cpf)
        _dataNascimento.value = FormatadorCampos.dataParaExibicao(doadora.dataNascimento)
        _email.value = doadora.email
        _telefone.value = FormatadorCampos.telefone(doadora.telefone)
        _cep.value = FormatadorCampos.cep(doadora.cep)
        _siglaEstado.value = doadora.siglaEstado
        _cidade.value = doadora.cidade
        _bairro.value = doadora.bairro
        _logradouro.value = doadora.logradouro
        _numero.value = doadora.numero
        _complemento.value = doadora.complemento.orEmpty()
        _cepConsultado.value = true
        _erroCep.value = null

        // Senhas nunca são recuperadas da API.
        _senha.value = ""
        _confirmarSenha.value = ""
    }



    fun onNomeChanged(value: String) {
        _nome.value = value.take(100)
    }

    fun onCpfChanged(value: String) {
        _cpf.value = FormatadorCampos.cpf(value)
    }

    fun onDataNascimentoChanged(value: String) {
        _dataNascimento.value = FormatadorCampos.data(value)
    }

    fun onTelefoneChanged(value: String) {
        _telefone.value = FormatadorCampos.telefone(value)
    }

    fun onCepChanged(value: String) {
        val numeros = value.filter { it.isDigit() }.take(8)
        val cepFormatado = FormatadorCampos.cep(numeros)

        if (cepFormatado == _cep.value) return

        chamadaCep?.cancel()
        chamadaCep = null

        _cep.value = cepFormatado
        _cepConsultado.value = false
        _consultandoCep.value = false
        _erroCep.value = null

        limparEndereco()

        if (numeros.length == 8) {
            consultarCep(numeros)
        }
    }

    fun onEmailChanged(value: String) {
        _email.value = value
    }

    fun onSiglaEstadoChanged(value: String) {
        _siglaEstado.value = value
    }

    fun onCidadeChanged(value: String) {
        _cidade.value = value
    }

    fun onBairroChanged(value: String) {
        _bairro.value = value
    }

    fun onLogradouroChanged(value: String) {
        _logradouro.value = value
    }

    fun onNumeroChanged(value: String) {
        _numero.value = value
    }

    fun onComplementoChanged(value: String) {
        _complemento.value = value.take(50)
    }

    fun onSenhaChanged(value: String) {
        _senha.value = value
    }

    fun onConfirmarSenhaChanged(value: String) {
        _confirmarSenha.value = value
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



    fun podeConfirmar(): Boolean {
        val numeroValido = ValidadorCampos.numeroValido(
            _numero.value.orEmpty().toIntOrNull()
        )

        val enderecoValido = ValidadorCampos.cepValido(_cep.value.orEmpty()) &&
                    _cepConsultado.value == true &&
                    _consultandoCep.value != true &&
                    _erroCep.value == null &&
                    !_siglaEstado.value.isNullOrBlank() &&
                    !_cidade.value.isNullOrBlank() &&
                    !_bairro.value.isNullOrBlank() &&
                    !_logradouro.value.isNullOrBlank() &&
                    numeroValido

        return _perfilCarregado.value == true &&
                _idDoadora.value != null &&
                ValidadorCampos.nomeValido(_nome.value.orEmpty()) &&
                ValidadorCampos.cpfValido(_cpf.value.orEmpty()) &&
                ValidadorCampos.dataNascimentoValida(_dataNascimento.value.orEmpty()) &&
                ValidadorCampos.emailValido(_email.value.orEmpty()) &&
                ValidadorCampos.telefoneValido(_telefone.value.orEmpty()) &&
                enderecoValido &&
                ValidadorCampos.senhaValida(_senha.value.orEmpty()) &&
                ValidadorCampos.senhasCoincidem(_senha.value.orEmpty(), _confirmarSenha.value.orEmpty()) &&
                _carregando.value != true &&
                _salvando.value != true
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
                if (call !== chamadaCep) return
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
                    _siglaEstado.value = endereco.uf
                    _cidade.value = endereco.cidade
                    _logradouro.value = endereco.logradouro.orEmpty()
                    _bairro.value = endereco.bairro.orEmpty()

                    _cepConsultado.value = true
                    _erroCep.value = null
                } else {
                    limparEndereco()
                    _erroCep.value = "CEP não encontrado. Confira os números."
                }
            }

            override fun onFailure(
                call: Call<Endereco>,
                t: Throwable
            ) {
                if (call.isCanceled || call !== chamadaCep) return
                if (_cep.value?.filter { it.isDigit() } != cep) return

                _consultandoCep.value = false
                limparEndereco()

                _erroCep.value = "Não foi possível consultar o CEP. Verifique sua conexão."

                Log.e("EditarPerfil", "Falha na consulta do CEP", t)
            }
        })
    }

    private fun limparEndereco() {
        _siglaEstado.value = ""
        _cidade.value = ""
        _logradouro.value = ""
        _bairro.value = ""
        _cepConsultado.value = false
    }





    fun atualizarDoadora() {
        if (_salvando.value == true) return

        if (!podeConfirmar()) {
            _mensagemErro.value = "Verifique os dados preenchidos antes de confirmar."
            return
        }

        val id = _idDoadora.value ?: return
        val numeroEndereco = _numero.value?.toIntOrNull() ?: return

        val request = AtualizarDoadoraRequest(
            nome = _nome.value.orEmpty().trim(),
            cpf = _cpf.value.orEmpty(),
            dataNascimento = FormatadorCampos.dataParaApi(_dataNascimento.value.orEmpty())!!,
            email = _email.value.orEmpty().trim(),
            logradouro = _logradouro.value.orEmpty(),
            cep = _cep.value.orEmpty(),
            bairro = _bairro.value.orEmpty(),
            numero = numeroEndereco,
            complemento = _complemento.value?.trim()?.takeIf { it.isNotEmpty() },
            cidade = _cidade.value.orEmpty(),
            siglaEstado = _siglaEstado.value.orEmpty(),
            telefone = _telefone.value.orEmpty().filter { it.isDigit() },
            senha = _senha.value.orEmpty()
        )

        _salvando.value = true
        _mensagemErro.value = null
        _atualizacaoConcluida.value = false

        val call = MaternaRetrofitFactory().getDoadoraService()
            .atualizarDoadora(id, request)

        chamadaAtualizacao = call

        call.enqueue(object : Callback<AtualizarDoadoraResponse> {
            override fun onResponse(
                call: Call<AtualizarDoadoraResponse>,
                response: Response<AtualizarDoadoraResponse>
            ) {
                if (call !== chamadaAtualizacao) return

                _salvando.value = false

                val resultado = response.body()

                if (response.isSuccessful && resultado?.status == true) {
                    _atualizacaoConcluida.value = true
                    _mensagemErro.value = null

                    Log.i(
                        "EditarPerfil",
                        "Dados da doadora atualizados com sucesso."
                    )
                } else {
                    _mensagemErro.value = resultado?.message ?: "Não foi possível atualizar os dados (HTTP ${response.code()})."

                    Log.e(
                        "EditarPerfil",
                        "Erro na atualização: HTTP ${response.code()}"
                    )
                }
            }

            override fun onFailure(
                call: Call<AtualizarDoadoraResponse>,
                t: Throwable
            ) {
                if (call.isCanceled || call !== chamadaAtualizacao) return

                _salvando.value = false
                _mensagemErro.value = "Não foi possível conectar à API. Verifique sua conexão."

                Log.e(
                    "EditarPerfil",
                    "Falha ao atualizar doadora",
                    t
                )
            }
        })
    }





    override fun onCleared() {
        chamadaAtual?.cancel()
        chamadaCep?.cancel()
        chamadaAtualizacao?.cancel()
        super.onCleared()
    }
}