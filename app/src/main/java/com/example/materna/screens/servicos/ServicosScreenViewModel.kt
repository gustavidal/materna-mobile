package com.example.materna.screens.servicos

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materna.models.responses.InstituicaoProxima
import com.example.materna.models.responses.InstituicoesProximasResponse
import com.example.materna.models.responses.PerfilDoadoraResponse
import com.example.materna.models.responses.RotaResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import com.example.materna.services.osrm.OsrmRetrofitFactory
import org.osmdroid.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class ServicosEstado {
    INICIAL,
    CARREGANDO,
    SUCESSO,
    VAZIO,
    ERRO,
}

class ServicosScreenViewModel(
    context: Context,
) : ViewModel() {
    private val applicationContext = context.applicationContext

    private var idDoadoraCarregada: Int? = null

    private val _estado = MutableLiveData<ServicosEstado>()
    val estado: LiveData<ServicosEstado> = _estado

    private val _cep = MutableLiveData<String>()
    val cep: LiveData<String> = _cep

    private val _instituicoes = MutableLiveData<List<InstituicaoProxima>>()
    val instituicoes: LiveData<List<InstituicaoProxima>> = _instituicoes

    private val _mensagemErro = MutableLiveData<String>()
    val mensagemErro: LiveData<String> = _mensagemErro

    private val _localizacaoDoadora = MutableLiveData<Pair<Double, Double>?>()
    val localizacaoDoadora: LiveData<Pair<Double, Double>?> = _localizacaoDoadora

    private val _pontosRota = MutableLiveData<List<GeoPoint>>(emptyList())
    val pontosRota: LiveData<List<GeoPoint>> = _pontosRota

    private val _carregandoRota = MutableLiveData(false)
    val carregandoRota: LiveData<Boolean> = _carregandoRota

    private val _erroRota = MutableLiveData<String?>(null)
    val erroRota: LiveData<String?> = _erroRota

    private var chamadaRota: Call<RotaResponse>? = null

    private var chamadaDoadora: Call<PerfilDoadoraResponse>? = null

    private var chamadaAtual: Call<InstituicoesProximasResponse>? = null

    fun onCepChanged(value: String) {
        val numeros = value.filter { it.isDigit() }.take(8)

        val cepFormatado = buildString {
            numeros.forEachIndexed { index, char ->
                if (index == 5) append('-')
                append(char)
            }
        }

        if (cepFormatado == _cep.value) return

        chamadaAtual?.cancel()
        chamadaAtual = null

        _cep.value = cepFormatado
        _instituicoes.value = emptyList()
        _mensagemErro.value = ""
        _estado.value = ServicosEstado.INICIAL
    }

    fun buscarLocalizacaoDoadora(idDoadora: Int) {
        if (idDoadoraCarregada == idDoadora) return

        idDoadoraCarregada = idDoadora

        chamadaDoadora?.cancel()
        _localizacaoDoadora.value = null

        val call =
            MaternaRetrofitFactory(applicationContext).getDoadoraService().buscarDoadora(idDoadora)

        chamadaDoadora = call

        call.enqueue(
            object : Callback<PerfilDoadoraResponse> {
                override fun onResponse(
                    call: Call<PerfilDoadoraResponse>,
                    response: Response<PerfilDoadoraResponse>,
                ) {
                    Log.d(
                        "MaternaAPI",
                        "GET doadora/$idDoadora - HTTP ${response.code()}",
                    )

                    Log.d(
                        "MaternaAPI",
                        "Resposta: ${response.body()}",
                    )

                    if (call !== chamadaDoadora) return

                    chamadaDoadora = null

                    if (response.isSuccessful && response.body()?.status == true) {
                        val doadora =
                            response.body()?.response?.doadora?.firstOrNull { it.id == idDoadora }

                        val cepDoadora = doadora?.cep.orEmpty()

                        if (
                            _cep.value.isNullOrBlank() &&
                                cepDoadora.filter { it.isDigit() }.length == 8
                        ) {
                            onCepChanged(cepDoadora)
                            buscarInstituicoes()
                        }

                        val latitude = doadora?.latitude?.toDoubleOrNull()
                        val longitude = doadora?.longitude?.toDoubleOrNull()

                        _localizacaoDoadora.value =
                            if (
                                latitude != null &&
                                    longitude != null &&
                                    latitude in -90.0..90.0 &&
                                    longitude in -180.0..180.0
                            ) {
                                latitude to longitude
                            } else {
                                null
                            }
                    } else {
                        _localizacaoDoadora.value = null
                        idDoadoraCarregada = null
                        Log.e(
                            "Servicos",
                            "Erro ao consultar doadora: ${response.code()}",
                        )
                    }
                }

                override fun onFailure(
                    call: Call<PerfilDoadoraResponse>,
                    t: Throwable,
                ) {
                    if (call.isCanceled || call !== chamadaDoadora) return

                    chamadaDoadora = null
                    _localizacaoDoadora.value = null
                    idDoadoraCarregada = null

                    Log.e("Servicos", "Falha ao consultar localização", t)
                }
            },
        )
    }

    fun buscarInstituicoes() {
        val cepAtual = _cep.value.orEmpty()

        if (cepAtual.filter { it.isDigit() }.length != 8) {
            _mensagemErro.value = "Informe um CEP válido com 8 dígitos."
            _estado.value = ServicosEstado.ERRO
            return
        }

        chamadaAtual?.cancel()

        _estado.value = ServicosEstado.CARREGANDO
        _mensagemErro.value = ""

        val call =
            MaternaRetrofitFactory(applicationContext)
                .getInstituicaoService()
                .buscarInstituicoesProximas(cepAtual)

        chamadaAtual = call

        call.enqueue(
            object : Callback<InstituicoesProximasResponse> {

                override fun onResponse(
                    call: Call<InstituicoesProximasResponse>,
                    response: Response<InstituicoesProximasResponse>,
                ) {
                    if (call !== chamadaAtual) return

                    chamadaAtual = null

                    val body = response.body()

                    if (response.isSuccessful && body?.status == true) {
                        val lista = body.response?.instituicao.orEmpty()

                        _instituicoes.value = lista

                        _estado.value =
                            if (lista.isEmpty()) {
                                ServicosEstado.VAZIO
                            } else {
                                ServicosEstado.SUCESSO
                            }

                        Log.i("Servicos", "Instituições encontradas: ${lista.size}")
                    } else {
                        _mensagemErro.value =
                            when (response.code()) {
                                400 -> "O CEP informado é inválido."
                                404 -> "Nenhum banco de leite encontrado para esse CEP."
                                else -> "Não foi possível buscar os bancos de leite."
                            }

                        _estado.value = ServicosEstado.ERRO

                        Log.e(
                            "Servicos",
                            "Erro HTTP ${response.code()}",
                        )
                    }
                }

                override fun onFailure(
                    call: Call<InstituicoesProximasResponse>,
                    t: Throwable,
                ) {
                    if (call.isCanceled || call !== chamadaAtual) return

                    chamadaAtual = null

                    _mensagemErro.value =
                        "Não foi possível conectar ao servidor. Verifique sua conexão."

                    _estado.value = ServicosEstado.ERRO

                    Log.e("Servicos", "Falha: ${t.message}", t)
                }
            },
        )
    }

    fun buscarRota(instituicao: InstituicaoProxima) {
        val origem = _localizacaoDoadora.value

        val latitudeDestino = instituicao.latitude.toDoubleOrNull()
        val longitudeDestino = instituicao.longitude.toDoubleOrNull()

        if (
            origem == null ||
                latitudeDestino == null ||
                longitudeDestino == null ||
                !latitudeDestino.isFinite() ||
                !longitudeDestino.isFinite() ||
                latitudeDestino !in -90.0..90.0 ||
                longitudeDestino !in -180.0..180.0
        ) {
            _erroRota.value = "Localização indisponível para traçar a rota."
            return
        }

        chamadaRota?.cancel()
        chamadaRota = null

        _pontosRota.value = emptyList()
        _carregandoRota.value = true
        _erroRota.value = null

        val coordenadas = "${origem.second},${origem.first};" + "$longitudeDestino,$latitudeDestino"

        val call = OsrmRetrofitFactory().getRotaService().buscarRota(coordenadas)

        chamadaRota = call

        call.enqueue(
            object : Callback<RotaResponse> {
                override fun onResponse(
                    call: Call<RotaResponse>,
                    response: Response<RotaResponse>,
                ) {
                    if (call !== chamadaRota) return

                    chamadaRota = null
                    _carregandoRota.value = false

                    val body = response.body()
                    val rota = body?.routes?.firstOrNull()

                    if (!response.isSuccessful || body?.code != "Ok" || rota == null) {
                        _erroRota.value = "Não foi possível calcular a rota."
                        return
                    }

                    val pontos =
                        rota.geometry.coordinates.mapNotNull { coordenada ->
                            val longitude = coordenada.getOrNull(0)
                            val latitude = coordenada.getOrNull(1)

                            if (
                                latitude != null &&
                                    longitude != null &&
                                    latitude.isFinite() &&
                                    longitude.isFinite() &&
                                    latitude in -90.0..90.0 &&
                                    longitude in -180.0..180.0
                            ) {
                                GeoPoint(latitude, longitude)
                            } else {
                                null
                            }
                        }

                    if (pontos.size < 2) {
                        _erroRota.value = "A rota retornada é inválida."
                        return
                    }

                    _pontosRota.value = pontos

                    Log.d("Servicos", "Rota carregada: ${pontos.size} pontos")
                }

                override fun onFailure(
                    call: Call<RotaResponse>,
                    t: Throwable,
                ) {
                    if (call.isCanceled || call !== chamadaRota) return

                    chamadaRota = null
                    _carregandoRota.value = false
                    _erroRota.value = "Erro de conexão ao calcular a rota."

                    Log.e("Servicos", "Erro ao buscar rota", t)
                }
            },
        )
    }

    fun cancelarRota() {
        chamadaRota?.cancel()
        chamadaRota = null

        _pontosRota.value = emptyList()
        _carregandoRota.value = false
        _erroRota.value = null
    }

    fun limparDadosSessao() {
        Log.d("Servicos", "Limpando dados da sessão")

        // Cancelar requisições pendentes
        chamadaAtual?.cancel()
        chamadaDoadora?.cancel()
        chamadaRota?.cancel()

        chamadaAtual = null
        chamadaDoadora = null
        chamadaRota = null

        // Limpar identificação da doadora
        idDoadoraCarregada = null

        // Limpar dados da busca
        _cep.value = ""
        _instituicoes.value = emptyList()
        _mensagemErro.value = ""
        _localizacaoDoadora.value = null

        // Limpar dados da rota
        _pontosRota.value = emptyList()
        _carregandoRota.value = false
        _erroRota.value = null

        // Restaurar estado inicial
        _estado.value = ServicosEstado.INICIAL

        Log.i("Servicos", "Dados da sessão limpos com sucesso")
    }

    override fun onCleared() {
        chamadaAtual?.cancel()
        chamadaDoadora?.cancel()
        chamadaRota?.cancel()
        super.onCleared()
    }
}
