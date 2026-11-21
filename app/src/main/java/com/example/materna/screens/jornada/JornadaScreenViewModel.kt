package com.example.materna.screens.jornada

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.materna.data.local.JornadaLocalProgress
import com.example.materna.data.local.MaternaDatabaseProvider
import com.example.materna.models.domain.JornadaEtapa
import com.example.materna.models.domain.JornadaStatus
import com.example.materna.models.requests.CriarVinculoEtapaRequest
import com.example.materna.models.responses.CriarVinculoEtapaResponse
import com.example.materna.models.responses.InstituicaoApiResponse
import com.example.materna.models.responses.JornadaResponse
import com.example.materna.models.responses.VinculoEtapaDoadoraResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.orEmpty

enum class JornadaEstado {
    CARREGANDO,
    SEM_BANCO,
    COM_BANCO,
    ERRO
}

class JornadaScreenViewModel(
    context: Context
) : ViewModel() {
    private val applicationContext = context.applicationContext

    private val jornadaLocalProgressDao = MaternaDatabaseProvider
        .getDatabase(applicationContext)
        .jornadaLocalProgressDao()

    private val _estado = MutableLiveData<JornadaEstado>()
    val estado: LiveData<JornadaEstado> = _estado

    private val _etapas = MutableLiveData<List<JornadaEtapa>>()
    val etapas: LiveData<List<JornadaEtapa>> = _etapas

    private val _progresso = MutableLiveData<Int>()
    val progresso: LiveData<Int> = _progresso

    private val _mensagemErro = MutableLiveData<String>()
    val mensagemErro: LiveData<String> = _mensagemErro



    private var idInstituicaoSelecionada: Int? = null
    private var idDoadoraAtual: Int? = null
    private var idJornadaAtual: Int? = null
    private var idPrimeiraEtapaReal: Int? = null
    private var criandoVinculo = false
    private var concluindoEtapa = false





    fun concluirEtapaEstatica(
        idEtapa: String,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        val idDoadora = idDoadoraAtual
        val idJornada = idJornadaAtual

        if (idDoadora == null || idJornada == null) {
            onErro("Não foi possível identificar a jornada.")
            return
        }

        val idsEstaticos = listOf(
            "conheca_doacao",
            "orientacoes",
            "questionario"
        )

        if (idEtapa !in idsEstaticos) {
            onErro("Etapa inválida.")
            return
        }

        val etapa = _etapas.value
            ?.firstOrNull { it.id == idEtapa }

        if (
            etapa == null ||
            etapa.status == JornadaStatus.BLOQUEADA
        ) {
            onErro("Esta etapa ainda não está disponível.")
            return
        }

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    jornadaLocalProgressDao.salvarConclusao(
                        JornadaLocalProgress(
                            idDoadora = idDoadora,
                            idJornada = idJornada,
                            idEtapa = idEtapa
                        )
                    )
                }

                val finalizar: () -> Unit = {
                    concluindoEtapa = false

                    val proximaEtapa = when (idEtapa) {
                        "conheca_doacao" -> "orientacoes"
                        "orientacoes" -> "questionario"
                        else -> null
                    }

                    _etapas.value = _etapas.value.orEmpty().map {
                        when {
                            it.id == idEtapa -> it.copy(status = JornadaStatus.CONCLUIDA)

                            it.id == proximaEtapa && it.status == JornadaStatus.BLOQUEADA -> it.copy(status = JornadaStatus.ATUAL)

                            else -> it
                        }
                    }

                    calcularProgresso()
                    onSucesso()
                }

                if (idEtapa == "conheca_doacao") {
                    garantirVinculoPrimeiraEtapa(
                        onSucesso = finalizar,
                        onErro = { erro ->
                            concluindoEtapa = false
                            onErro(erro)
                        }
                    )
                } else {
                    finalizar()
                }

            } catch (e: Exception) {
                Log.e("Jornada", "Erro ao salvar conclusão", e)
                concluindoEtapa = false
                onErro("Não foi possível salvar a conclusão.")
            }
        }
    }

    private fun calcularProgresso() {
        val lista = _etapas.value.orEmpty()

        if (lista.isEmpty()) {
            _progresso.value = 0
            return
        }

        val concluidas = lista.count {
            it.status == JornadaStatus.CONCLUIDA
        }

        _progresso.value = (concluidas * 100) / lista.size
    }

    private fun testarBuscaInstituicao(idInstituicao: Int) {
        val call = MaternaRetrofitFactory(applicationContext)
            .getInstituicaoService()
            .buscarInstituicao(idInstituicao)

        call.enqueue(object : Callback<InstituicaoApiResponse> {
            override fun onResponse(
                call: Call<InstituicaoApiResponse>,
                response: Response<InstituicaoApiResponse>
            ) {
                Log.i("Jornada", "Response: ${response.body()}")

                if (response.isSuccessful && response.body()?.status == true) {
                    val instituicao = response.body()
                        ?.response
                        ?.instituicao
                        ?.firstOrNull()

                    val jornadasAtivas = instituicao
                        ?.jornadas
                        .orEmpty()
                        .filter { it.isAtivo == 1 }

                    if (jornadasAtivas.size == 1) {
                        val jornada = jornadasAtivas.first()
                        val idDoadora = idDoadoraAtual
                        idPrimeiraEtapaReal = jornada.etapas
                            .minByOrNull { it.ordem }
                            ?.id
                        idJornadaAtual = jornada.id

                        if (idDoadora == null) {
                            _mensagemErro.value = "Não foi possível identificar a doadora."
                            _estado.value = JornadaEstado.ERRO
                            return
                        }

                        viewModelScope.launch {
                            try {
                                val etapasConcluidas = withContext(Dispatchers.IO) {
                                    jornadaLocalProgressDao
                                        .observarProgresso(
                                            idDoadora = idDoadora,
                                            idJornada = jornada.id
                                        )
                                        .first()
                                        .filter { it.concluida }
                                        .map { it.idEtapa }
                                        .toSet()
                                }

                                val idPrimeiraEtapa = idPrimeiraEtapaReal

                                if (idPrimeiraEtapa == null) {
                                    _mensagemErro.value = "Esta jornada não possui etapas disponíveis."
                                    _estado.value = JornadaEstado.ERRO
                                    return@launch
                                }

                                verificarVinculoPrimeiraEtapa(
                                    idDoadora = idDoadora,
                                    idJornada = jornada.id,
                                    idInstituicao = idInstituicao,
                                    idEtapa = idPrimeiraEtapa,

                                    onResultado = { possuiVinculo ->
                                        _etapas.value = montarEtapasJornada(
                                            jornada = jornada,
                                            etapasConcluidas = etapasConcluidas,
                                            possuiVinculo = possuiVinculo
                                        )

                                        calcularProgresso()

                                        _estado.value = JornadaEstado.COM_BANCO

                                        Log.i(
                                            "Jornada",
                                            "Vínculo confirmado: $possuiVinculo"
                                        )
                                    },

                                    onErro = { erro ->
                                        _mensagemErro.value = erro
                                        _estado.value = JornadaEstado.ERRO
                                    }
                                )

                                Log.i(
                                    "Jornada",
                                    "Progresso local recuperado: $etapasConcluidas"
                                )
                            } catch (e: Exception) {
                                _mensagemErro.value = "Não foi possível recuperar o progresso da jornada."
                                _estado.value = JornadaEstado.ERRO

                                Log.e(
                                    "Jornada",
                                    "Erro ao consultar o Room",
                                    e
                                )
                            }
                        }
                    } else {
                        _mensagemErro.value = "Este banco de leite ainda não possui uma jornada disponível."
                        _estado.value = JornadaEstado.ERRO
                        Log.e("Jornada", "Nenhuma jornada ativa encontrada.")
                    }

                    Log.i("Jornada", "Instituição: ${instituicao?.nome}")
                    Log.i("Jornada", "Jornadas ativas: ${jornadasAtivas.size}")

                    jornadasAtivas.forEach { jornada ->
                        Log.i("Jornada", "Jornada: ${jornada.nome} | Etapas: ${jornada.etapas.size}")
                    }
                } else {
                    _mensagemErro.value = "Não foi possível carregar sua jornada. Tente novamente."
                    _estado.value = JornadaEstado.ERRO

                    val erroApi = response.errorBody()?.string()

                    Log.e(
                        "Jornada",
                        """
                               Erro ao buscar instituição:
                               HTTP: ${response.code()}
                               URL: ${call.request().url}
                               Resposta do servidor: $erroApi
                               """.trimIndent()
                    )
                }
            }

            override fun onFailure(
                call: Call<InstituicaoApiResponse>,
                t: Throwable
            ) {
                if (call.isCanceled) return
                _mensagemErro.value = "Não foi possível conectar ao servidor. " + "Verifique sua conexão."
                _estado.value = JornadaEstado.ERRO
                Log.e("Jornada", "Falha: ${t.message}", t)
            }
        })
    }

    private fun montarEtapasJornada(
        jornada: JornadaResponse,
        etapasConcluidas: Set<String>,
        possuiVinculo: Boolean
    ): List<JornadaEtapa> {
        val idsEstaticos = listOf(
            "conheca_doacao",
            "orientacoes",
            "questionario"
        )

        val conclusoesValidas = if (possuiVinculo) {
            etapasConcluidas
        } else {
            etapasConcluidas - setOf(
                "conheca_doacao",
                "orientacoes",
                "questionario"
            )
        }

        val primeiraPendente = idsEstaticos.indexOfFirst {
            it !in conclusoesValidas
        }

        val etapasIniciais = listOf(
            JornadaEtapa(
                id = "conheca_doacao",
                titulo = "Conheça a doação",
                descricao = "Entenda o impacto da doação",
                status = JornadaStatus.BLOQUEADA
            ),
            JornadaEtapa(
                id = "orientacoes",
                titulo = "Orientações",
                descricao = "Conheça os cuidados necessários",
                status = JornadaStatus.BLOQUEADA
            ),
            JornadaEtapa(
                id = "questionario",
                titulo = "Questionário inicial",
                descricao = "Responda às perguntas iniciais",
                status = JornadaStatus.BLOQUEADA
            )
        ).mapIndexed { index, etapa ->
            etapa.copy(
                status = when {
                    etapa.id in conclusoesValidas -> JornadaStatus.CONCLUIDA
                    index == primeiraPendente -> JornadaStatus.ATUAL
                    else -> JornadaStatus.BLOQUEADA
                }
            )
        }

        val etapasInstituicao = jornada.etapas
            .sortedBy { it.ordem }
            .map { etapa ->
                JornadaEtapa(
                    id = "instituicao_${etapa.id}",
                    titulo = etapa.titulo,
                    descricao = etapa.descricao,
                    status = JornadaStatus.BLOQUEADA
                )
            }

        return etapasIniciais + etapasInstituicao
    }

    fun carregarInstituicao(
        idInstituicao: Int,
        idDoadora: Int
    ) {
        if (idInstituicao <= 0 || idDoadora <= 0) return

        if (
            idInstituicaoSelecionada == idInstituicao &&
            idDoadoraAtual == idDoadora &&
            _estado.value == JornadaEstado.COM_BANCO
        ) return

        idInstituicaoSelecionada = idInstituicao
        idDoadoraAtual = idDoadora

        _estado.value = JornadaEstado.CARREGANDO
        _mensagemErro.value = ""

        testarBuscaInstituicao(idInstituicao)
    }

    private fun garantirVinculoPrimeiraEtapa(
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        val idDoadora = idDoadoraAtual
        val idJornada = idJornadaAtual
        val idInstituicao = idInstituicaoSelecionada
        val idEtapa = idPrimeiraEtapaReal

        if (
            idDoadora == null ||
            idJornada == null ||
            idInstituicao == null ||
            idEtapa == null
        ) {
            onErro("Dados da jornada incompletos.")
            return
        }

        if (criandoVinculo) {
            onErro("A vinculação já está em andamento. Aguarde.")
            return
        }

        concluindoEtapa = false

        criandoVinculo = true

        val service = MaternaRetrofitFactory(applicationContext)
            .getVinculoEtapaDoadoraService()

        service.buscarVinculosDoadora(idDoadora)
            .enqueue(object : Callback<VinculoEtapaDoadoraResponse> {

                override fun onResponse(
                    call: Call<VinculoEtapaDoadoraResponse>,
                    response: Response<VinculoEtapaDoadoraResponse>
                ) {
                    val erroApi = response.errorBody()?.string().orEmpty()

                    val semVinculos =
                        response.code() == 404 &&
                        erroApi.contains(
                            "\"status_code\"\\s*:\\s*404".toRegex()
                        ) &&
                        erroApi.contains("Não foram encontrados dados")

                    Log.d(
                        "JornadaVinculo",
                        "Consulta antes do POST: HTTP ${response.code()} | $erroApi"
                    )

                    if (
                        !semVinculos &&
                        (!response.isSuccessful || response.body()?.status != true)
                    ) {
                        criandoVinculo = false

                        onErro("Não foi possível verificar os vínculos existentes.")
                        return
                    }

                    if (semVinculos) {
                        Log.i(
                            "JornadaVinculo",
                            "Nenhum vínculo encontrado. Prosseguindo para o POST."
                        )
                    }

                    val vinculoExistente = response.body()
                        ?.response
                        ?.jornadas
                        .orEmpty()
                        .firstOrNull {
                            it.id == idJornada && it.instituicao.id == idInstituicao
                        }
                        ?.etapas
                        ?.firstOrNull { it.id == idEtapa }
                        ?.vinculos
                        ?.isNotEmpty() == true

                    if (vinculoExistente) {
                        criandoVinculo = false
                        onSucesso()
                        return
                    }

                    val request = CriarVinculoEtapaRequest(
                        idDoadora = idDoadora,
                        idEtapa = idEtapa,
                        idInstituicao = idInstituicao,
                        idJornada = idJornada
                    )

                    service.criarVinculoEtapa(request)
                        .enqueue(object : Callback<CriarVinculoEtapaResponse> {

                            override fun onResponse(
                                call: Call<CriarVinculoEtapaResponse>,
                                response: Response<CriarVinculoEtapaResponse>
                            ) {
                                criandoVinculo = false

                                if (
                                    response.isSuccessful &&
                                    response.body()?.status == true
                                ) {
                                    Log.i(
                                        "Jornada",
                                        "Vínculo criado: ${response.body()?.response?.id}"
                                    )
                                    onSucesso()
                                } else {
                                    Log.e(
                                        "Jornada",
                                        "Erro ao criar vínculo: HTTP ${response.code()}"
                                    )
                                    onErro("Não foi possível criar o vínculo.")
                                }
                            }

                            override fun onFailure(
                                call: Call<CriarVinculoEtapaResponse>,
                                t: Throwable
                            ) {
                                criandoVinculo = false
                                Log.e("Jornada", "Falha no POST", t)
                                onErro("Falha de conexão ao criar o vínculo.")
                            }
                        })
                }

                override fun onFailure(
                    call: Call<VinculoEtapaDoadoraResponse>,
                    t: Throwable
                ) {
                    criandoVinculo = false
                    Log.e("Jornada", "Falha no GET de vínculos", t)
                    onErro("Falha de conexão ao verificar o vínculo.")
                }
            })
    }

    private fun verificarVinculoPrimeiraEtapa(
        idDoadora: Int,
        idJornada: Int,
        idInstituicao: Int,
        idEtapa: Int,
        onResultado: (Boolean) -> Unit,
        onErro: (String) -> Unit
    ) {
        MaternaRetrofitFactory(applicationContext)
            .getVinculoEtapaDoadoraService()
            .buscarVinculosDoadora(idDoadora)
            .enqueue(object : Callback<VinculoEtapaDoadoraResponse> {

                override fun onResponse(
                    call: Call<VinculoEtapaDoadoraResponse>,
                    response: Response<VinculoEtapaDoadoraResponse>
                ) {
                    val erroApi = response.errorBody()?.string().orEmpty()

                    Log.d(
                        "JornadaVinculo",
                        "HTTP: ${response.code()} | " +
                            "Status: ${response.body()?.status} | " +
                            "Erro: $erroApi"
                    )

                    if (
                        response.code() == 404 &&
                        erroApi.contains(
                            "\"status_code\"\\s*:\\s*404".toRegex()
                        ) &&
                        erroApi.contains("Não foram encontrados dados")
                    ) {
                        Log.i(
                            "Jornada",
                            "Nenhum vínculo encontrado. Liberando a Jornada."
                        )

                        onResultado(false)
                        return
                    }
              
                    val semVinculos =
                        response.code() == 404 &&
                        erroApi.contains(
                            "\"status_code\"\\s*:\\s*404".toRegex()
                        ) &&
                        erroApi.contains("Não foram encontrados dados")

                    if (
                        !semVinculos &&
                        (!response.isSuccessful || response.body()?.status != true)
                    ) {
                        criandoVinculo = false

                        Log.e(
                            "JornadaVinculo",
                            "Falha ao consultar vínculos: HTTP ${response.code()} | $erroApi"
                        )

                        onErro("Não foi possível verificar os vínculos existentes.")
                        return
                    }

                    if (semVinculos) {
                        Log.i(
                            "JornadaVinculo",
                            "Doadora sem vínculos. Criando primeiro vínculo."
                        )
                    }

                    Log.d(
                        "JornadaVinculo",
                        "IDs esperados: jornada=$idJornada, " +
                            "instituicao=$idInstituicao, etapa=$idEtapa"
                    )

                    response.body()?.response?.jornadas?.forEach { jornada ->
                        Log.d(
                            "JornadaVinculo",
                            "Jornada recebida: ${jornada.id} | " +
                                "Instituição: ${jornada.instituicao.id}"
                        )

                        jornada.etapas.forEach { etapa ->
                            Log.d(
                                "JornadaVinculo",
                                "Etapa recebida: ${etapa.id} | " +
                                    "Vínculos: ${etapa.vinculos.size}"
                            )
                        }
                    }

                    val possuiVinculo = response.body()
                        ?.response
                        ?.jornadas
                        .orEmpty()
                        .any { jornada ->
                            jornada.id == idJornada &&
                                jornada.instituicao.id == idInstituicao &&
                                jornada.etapas.any { etapa ->
                                    etapa.id == idEtapa &&
                                        etapa.vinculos.isNotEmpty()
                                }
                        }

                    onResultado(possuiVinculo)
                }

                override fun onFailure(
                    call: Call<VinculoEtapaDoadoraResponse>,
                    t: Throwable
                ) {
                    if (call.isCanceled) return

                    Log.e("Jornada", "Erro ao verificar vínculo", t)

                    onErro(
                        "Não foi possível verificar o vínculo. Tente novamente."
                    )
                }
            })
    }

    fun recarregarJornada() {
        val idInstituicao = idInstituicaoSelecionada ?: run {
            _estado.value = JornadaEstado.SEM_BANCO
            return
        }

        if (_estado.value == JornadaEstado.CARREGANDO) return

        _estado.value = JornadaEstado.CARREGANDO
        _mensagemErro.value = ""

        testarBuscaInstituicao(idInstituicao)
    }

    init {
        _estado.value = JornadaEstado.SEM_BANCO
    }
}