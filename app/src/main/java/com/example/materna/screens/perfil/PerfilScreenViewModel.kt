package com.example.materna.screens.perfil

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.materna.data.local.AppSettingsDao
import com.example.materna.models.requests.AtualizarFotoRequest
import com.example.materna.models.responses.AtualizarFotoResponse
import com.example.materna.models.responses.CloudinaryUploadResponse
import com.example.materna.models.responses.PerfilDoadora
import com.example.materna.models.responses.PerfilDoadoraResponse
import com.example.materna.services.cloudinary.CloudinaryRetrofitFactory
import com.example.materna.services.materna.MaternaRetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilScreenViewModel(
    private val settingsDao: AppSettingsDao,
    context: Context
) : ViewModel() {
    private val applicationContext = context.applicationContext

    private val _doadora = MutableLiveData<PerfilDoadora?>()
    val doadora: LiveData<PerfilDoadora?> = _doadora

    private val _carregando = MutableLiveData(false)
    val carregando: LiveData<Boolean> = _carregando

    private val _mensagemErro = MutableLiveData<String?>()
    val mensagemErro: LiveData<String?> = _mensagemErro

    private val _atualizandoFoto = MutableLiveData(false)
    val atualizandoFoto: LiveData<Boolean> = _atualizandoFoto

    private val _fotoPerfil = MutableLiveData<String?>(null)
    val fotoPerfil: LiveData<String?> = _fotoPerfil

    private var chamadaAtualizacaoFoto: Call<AtualizarFotoResponse>? = null

    fun atualizarFotoPerfil(
        context: Context,
        uri: Uri
    ) {
        if (_atualizandoFoto.value == true) return

        _atualizandoFoto.value = true
        _mensagemErro.value = null

        enviarFotoCloudinary(
            context = context,
            uri = uri,
            onSucesso = { url ->
                Log.d("PerfilFoto", "Upload concluído no Cloudinary")

                val call = MaternaRetrofitFactory(applicationContext)
                    .getDoadoraService()
                    .atualizarFoto(
                        AtualizarFotoRequest(foto = url)
                    )

                chamadaAtualizacaoFoto = call

                call.enqueue(object : Callback<AtualizarFotoResponse> {
                    override fun onResponse(
                        call: Call<AtualizarFotoResponse>,
                        response: Response<AtualizarFotoResponse>
                    ) {
                        if (call !== chamadaAtualizacaoFoto) return

                        _atualizandoFoto.value = false

                        val resultado = response.body()

                        if (response.isSuccessful &&
                            resultado?.status == true
                        ) {
                            _fotoPerfil.value = resultado.response?.foto ?: url
                            _mensagemErro.value = null
                            Log.i("PerfilFoto", "Foto atualizada no Materna")
                        } else {
                            val detalhe = response.errorBody()?.string()
                            Log.e("PerfilFoto", "PATCH HTTP ${response.code()}: $detalhe")
                            _mensagemErro.value = resultado?.message ?: "Não foi possível salvar a foto no perfil."
                        }
                    }

                    override fun onFailure(
                        call: Call<AtualizarFotoResponse>,
                        t: Throwable
                    ) {
                        if (call.isCanceled || call !== chamadaAtualizacaoFoto) return

                        _atualizandoFoto.value = false

                        _mensagemErro.value = "Erro de conexão ao atualizar a foto."

                        Log.e("PerfilFoto", "Falha no PATCH", t)
                    }
                })
            },
            onErro = { erro ->
                _atualizandoFoto.value = false
                _mensagemErro.value = erro

                Log.e("PerfilFoto", "Erro no Cloudinary: $erro")
            }
        )
    }

    private var chamadaAtual: Call<PerfilDoadoraResponse>? = null

    init {
        carregarPerfil()
    }

    private fun carregarPerfil() {
        viewModelScope.launch {
            try {
                val settings = settingsDao.observeSettings().first()
                val idDoadora = settings?.idDoadora

                if (idDoadora != null && idDoadora > 0) {
                    buscarDoadora(idDoadora)
                } else {
                    _mensagemErro.value = "Nenhuma doadora identificada. Faça seu cadastro ou entre na sua conta."
                }
            } catch (e: Exception) {
                Log.e("PerfilDoadora", "Erro ao recuperar ID da doadora", e)

                _mensagemErro.value = "Não foi possível recuperar os dados da doadora."
            }
        }
    }

    fun buscarDoadora(id: Int) {
        if (id <= 0) {
            _mensagemErro.value = "ID da doadora inválido."
            return
        }

        chamadaAtual?.cancel()

        _carregando.value = true
        _mensagemErro.value = null

        val call = MaternaRetrofitFactory(applicationContext).getDoadoraService()
            .buscarDoadora(id)

        chamadaAtual = call

        call.enqueue(object : Callback<PerfilDoadoraResponse> {
            override fun onResponse(
                call: Call<PerfilDoadoraResponse>,
                response: Response<PerfilDoadoraResponse>
            ) {
                if (call !== chamadaAtual) return

                _carregando.value = false

                Log.i(
                    "PerfilDoadora",
                    "Response: ${response.body()}"
                )

                val resultado = response.body()

                if (response.isSuccessful && resultado?.status == true) {
                    val doadoraEncontrada = resultado.response
                        ?.doadora
                        ?.firstOrNull { it.id == id }

                    if (doadoraEncontrada != null) {
                        _doadora.value = doadoraEncontrada
                        _mensagemErro.value = null
                        _fotoPerfil.value = doadoraEncontrada.foto
                    } else {
                        _doadora.value = null
                        _mensagemErro.value = "Doadora não encontrada."
                    }
                } else {
                    _doadora.value = null
                    _mensagemErro.value = resultado?.message ?: "Erro ao consultar perfil (HTTP ${response.code()})."

                    Log.e(
                        "PerfilDoadora",
                        "Erro HTTP ${response.code()}: ${_mensagemErro.value}"
                    )
                }
            }

            override fun onFailure(
                call: Call<PerfilDoadoraResponse>,
                t: Throwable
            ) {
                if (call.isCanceled || call !== chamadaAtual) return

                _carregando.value = false
                _doadora.value = null

                _mensagemErro.value = "Não foi possível conectar à API. Verifique se o backend está ativo."

                Log.e(
                    "PerfilDoadora",
                    "Falha: ${t.message}",
                    t
                )
            }
        })
    }

    fun enviarFotoCloudinary(
        context: Context,
        uri: Uri,
        onSucesso: (String) -> Unit,
        onErro: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val arquivo = withContext(Dispatchers.IO) {
                    val resolver = context.contentResolver

                    val mimeType = resolver.getType(uri) ?: "image/jpeg"

                    val bytes = resolver.openInputStream(uri)?.use {
                        it.readBytes()
                    } ?: throw Exception("Não foi possível ler a imagem.")

                    val requestBody = bytes.toRequestBody(
                        mimeType.toMediaTypeOrNull()
                    )

                    val extensao = MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(mimeType) ?: "jpg"

                    MultipartBody.Part.createFormData(
                        "file",
                        "perfil.$extensao",
                        requestBody
                    )
                }

                val preset = "materna-perfis"
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                CloudinaryRetrofitFactory().getCloudinaryService()
                    .enviarImagem(arquivo, preset)
                    .enqueue(object : Callback<CloudinaryUploadResponse> {
                        override fun onResponse(
                            call: Call<CloudinaryUploadResponse>,
                            response: Response<CloudinaryUploadResponse>
                        ) {
                            val url = response.body()?.secureUrl

                            if (response.isSuccessful && !url.isNullOrBlank()) {
                                onSucesso(url)
                            } else {
                                val erro = response.errorBody()?.string()
                                onErro(
                                    "Falha no upload (${response.code()}): $erro"
                                )
                            }
                        }

                        override fun onFailure(
                            call: Call<CloudinaryUploadResponse>,
                            t: Throwable
                        ) {
                            onErro(
                                t.message ?: "Erro de conexão com o Cloudinary."
                            )
                        }
                    })

            } catch (e: Exception) {
                onErro(e.message ?: "Erro ao preparar a imagem.")
            }
        }
    }

    override fun onCleared() {
        chamadaAtual?.cancel()
        chamadaAtualizacaoFoto?.cancel()
        super.onCleared()
    }
}