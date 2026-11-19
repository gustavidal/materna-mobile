package com.example.materna.screens.login

import android.content.Context
import android.util.Log
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
        if (_carregando.value == true) {
            Log.w("LoginDoadora", "Login ignorado: autenticação já em andamento.")
            return
        }

        val email = _email.value.orEmpty().trim()
        val senha = _senha.value.orEmpty()

        Log.d("LoginDoadora", "Iniciando processo de autenticação.")

        if (email.isBlank() || senha.isBlank()) {
            Log.w("LoginDoadora", "Validação falhou: campos obrigatórios vazios.")
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

        Log.d("LoginDoadora", "Preparando requisição de login.")

        try {
            val call = MaternaRetrofitFactory(context.applicationContext)
                .getAuthService()
                .realizarLogin(login)

            chamadaLogin = call

            Log.d("LoginDoadora", "Enviando requisição para a API.")

            call.enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    Log.d("LoginDoadora", "Resposta HTTP recebida: ${response.code()}")

                    if (response.isSuccessful) {
                        val resultado = response.body()

                        if (resultado?.status == true) {
                            Log.d("LoginDoadora", "API confirmou a autenticação.")

                            val dados = resultado.response

                            if (dados?.token.isNullOrBlank() || dados?.doadora == null) {
                                Log.e("LoginDoadora", "Resposta incompleta: token ou doadora ausente.")
                                _erroLogin.value = "A API não retornou os dados de autenticação."
                                _carregando.value = false
                                return
                            }

                            val sessionManager = SessionManager(
                                context.applicationContext
                            )

                            viewModelScope.launch {
                                try {
                                    val idDoadora = dados.doadora.id

                                    require(idDoadora > 0) {
                                        "ID da doadora inválido."
                                    }

                                    Log.d("LoginDoadora", "Dados de autenticação validados.")
                                    Log.d("LoginDoadora", "Iniciando persistência da sessão.")

                                    val tokenSalvo = withContext(Dispatchers.IO) {
                                        val database = MaternaDatabaseProvider.getDatabase(
                                            context.applicationContext
                                        )

                                        database.appSettingsDao().salvarIdDoadora(idDoadora)
                                        database.appSettingsDao().salvarEmailDoadora(email)

                                        sessionManager.salvarToken(dados.token)
                                    }

                                    if (tokenSalvo) {
                                        Log.i("LoginDoadora", "Login realizado com sucesso.")
                                        Log.i("LoginDoadora", "Sessão persistida com sucesso.")
                                        _erroLogin.value = null
                                        _loginRealizado.value = true
                                    } else {
                                        Log.e("LoginDoadora", "Falha ao salvar token da sessão.")
                                        _erroLogin.value = "Não foi possível salvar sua sessão. Tente novamente."
                                    }

                                } catch (e: Exception) {
                                    Log.e("LoginDoadora", "Erro ao persistir sessão.", e)
                                    _erroLogin.value = "Não foi possível concluir o login. Tente novamente."
                                } finally {
                                    Log.d("LoginDoadora", "Finalizando processamento do login.")
                                    _carregando.value = false
                                }
                            }
                        } else {
                            Log.w("LoginDoadora", "API retornou status de autenticação negativo.")
                            _erroLogin.value = resultado?.message ?: "Não foi possível realizar o login."
                            _carregando.value = false
                        }
                    } else {
                        Log.w("LoginDoadora", "Falha HTTP na autenticação: ${response.code()}")

                        val erro = try {
                            response.errorBody()?.string()?.let {
                                Gson().fromJson(it, LoginResponse::class.java)
                            }
                        } catch (e: Exception) {
                            Log.e("LoginDoadora", "Erro ao interpretar resposta da API.", e)
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
                    if (call.isCanceled) {
                        Log.d("LoginDoadora", "Requisição de login cancelada.")
                        return
                    }

                    Log.e("LoginDoadora", "Falha de comunicação com a API.", t)

                    _carregando.value = false
                    _erroLogin.value = "Não foi possível conectar à API. Verifique sua conexão."
                }
            })

        } catch (e: Exception) {
            Log.e("LoginDoadora", "Erro ao preparar requisição de login.", e)

            _carregando.value = false
            _erroLogin.value = "Não foi possível iniciar o login. Tente novamente."
        }
    }

    override fun onCleared() {
        chamadaLogin?.cancel()
        super.onCleared()
    }
}