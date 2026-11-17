package com.example.materna.screens.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.materna.data.local.MaternaDatabaseProvider
import com.example.materna.models.requests.LoginRequest
import com.example.materna.models.responses.LoginResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import com.example.materna.session.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginScreenViewModel: ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    fun onEmailChanged( novoValor: String ) {
        _email.value = novoValor
    }



    private val _senha = MutableLiveData<String>()
    val senha: LiveData<String> = _senha

    fun onSenhaChanged( novoValor: String ) {
        _senha.value = novoValor
    }

    private val _mostrarSenha = MutableLiveData<Boolean>()
    val mostrarSenha: LiveData<Boolean> = _mostrarSenha

    fun toggleMostrarSenha() {
        _mostrarSenha.value = !(_mostrarSenha.value ?: false)
    }

    fun ocultarSenha() {
        _mostrarSenha.value = false
    }

    private val _carregando = MutableLiveData<Boolean>()
    val carregando: LiveData<Boolean> = _carregando

    private val _erroLogin = MutableLiveData<String?>()
    val erroLogin: LiveData<String?> = _erroLogin

    private val _loginRealizado = MutableLiveData<Boolean>()
    val loginRealizado: LiveData<Boolean> = _loginRealizado

    private var chamadaLogin: Call<LoginResponse>? = null

    fun realizarLogin(context: Context) {
        if (_carregando.value == true) return

        val email = _email.value.orEmpty().trim()
        val senha = _senha.value.orEmpty()

        if (email.isBlank() || senha.isBlank()) {
            _erroLogin.value = "Preencha o e-mail e a senha."
            return
        }

        val login = LoginRequest(
            email = email,
            senha = senha
        )

        _carregando.value = true
        _erroLogin.value = null
        _loginRealizado.value = false

        val call = MaternaRetrofitFactory(context.applicationContext)
            .getAuthService()
            .realizarLogin(login)

        chamadaLogin = call

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val resultado = response.body()

                    if (resultado?.status == true) {
                        val dados = resultado.response

                        if (dados?.token.isNullOrBlank() || dados?.doadora == null) {
                            _erroLogin.value = "A API não retornou os dados de autenticação."
                            return
                        }

                        val sessionManager = SessionManager(
                            context.applicationContext
                        )

                        _carregando.value = true

                        viewModelScope.launch {
                            try {
                                val idDoadora = dados.doadora.id

                                require(idDoadora > 0) {
                                    "ID da doadora inválido."
                                }

                                val tokenSalvo = withContext(Dispatchers.IO) {
                                    val database = MaternaDatabaseProvider.getDatabase(
                                        context.applicationContext
                                    )

                                    database.appSettingsDao().salvarIdDoadora(idDoadora)

                                    sessionManager.salvarToken(dados.token)
                                }

                                if (tokenSalvo) {
                                    _erroLogin.value = null
                                    _loginRealizado.value = true
                                } else {
                                    _erroLogin.value = "Não foi possível salvar sua sessão. Tente novamente."
                                }
                            } catch (e: Exception) {
                                _erroLogin.value = "Não foi possível concluir o login. Tente novamente."
                            } finally {
                                _carregando.value = false
                            }
                        }
                    } else {
                        _erroLogin.value = resultado?.message ?: "Não foi possível realizar o login."
                    }
                } else {
                    val erro = try {
                        response.errorBody()?.string()?.let {
                            Gson().fromJson(it, LoginResponse::class.java)
                        }
                    } catch (e: Exception) {
                        null
                    }

                    _erroLogin.value = when (response.code()) {
                        401 -> "E-mail ou senha incorretos."
                        else -> erro?.message ?: "Erro ao realizar login (HTTP ${response.code()})."
                    }

                    _carregando.value = false
                }
            }

            override fun onFailure(
                call: Call<LoginResponse>,
                t: Throwable
            ) {
                if (call.isCanceled) return

                _carregando.value = false
                _erroLogin.value = "Não foi possível conectar à API. Verifique sua conexão."
            }
        })
    }

    override fun onCleared() {
        chamadaLogin?.cancel()
        super.onCleared()
    }
}