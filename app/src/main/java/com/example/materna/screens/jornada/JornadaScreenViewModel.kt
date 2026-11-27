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
import com.example.materna.models.requests.CancelarJornadaRequest
import com.example.materna.models.requests.ConcluirEtapaRequest
import com.example.materna.models.requests.CriarVinculoEtapaRequest
import com.example.materna.models.requests.EnviarDocumentoRequest
import com.example.materna.models.responses.CriarVinculoEtapaResponse
import com.example.materna.models.responses.DocumentoDoadoraResponse
import com.example.materna.models.responses.DocumentoResponse
import com.example.materna.models.responses.DocumentosDoadoraResponse
import com.example.materna.models.responses.EnviarDocumentoResponse
import com.example.materna.models.responses.InstituicaoApiResponse
import com.example.materna.models.responses.JornadaResponse
import com.example.materna.models.responses.VinculoEtapa
import com.example.materna.models.responses.VinculoEtapaDoadoraResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

enum class JornadaEstado {
    CARREGANDO,
    SEM_BANCO,
    COM_BANCO,
    AGUARDANDO_FINALIZACAO,
    FINALIZADA,
    ERRO
}

enum class DocumentoEstado {
    INICIAL,
    CARREGANDO,
    SUCESSO,
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

    private val _nomeInstituicao = MutableLiveData<String>()
    val nomeInstituicao: LiveData<String> = _nomeInstituicao

    private val _idColeta = MutableLiveData<Int?>(null)
    val idColeta: LiveData<Int?> = _idColeta





    private var idInstituicaoSelecionada: Int? = null
    private var idDoadoraAtual: Int? = null
    val idDoadoraSelecionada: Int? get() = idDoadoraAtual
    private var idJornadaAtual: Int? = null
    val idJornadaSelecionada: Int? get() = idJornadaAtual
    private var jornadaCarregada: JornadaResponse? = null
    private var idPrimeiraEtapaReal: Int? = null
    private var etapasInstitucionaisConcluidas: Set<String> = emptySet()
    private var criandoVinculo = false
    private var concluindoEtapa = false

    private val _cancelandoJornada = MutableLiveData(false)
    val cancelandoJornada: LiveData<Boolean> = _cancelandoJornada

    private val _erroCancelamento = MutableLiveData<String?>(null)
    val erroCancelamento: LiveData<String?> = _erroCancelamento

    private val _jornadaCancelada = MutableLiveData(false)
    val jornadaCancelada: LiveData<Boolean> = _jornadaCancelada

    private var cancelamentoConfirmadoApi = false

    // Ciclos restantes da etapa repetível (id real da etapa -> ciclos). Definidos pela instituição.
    private var ciclosRestantesPorEtapa: Map<Int, Int> = emptyMap()

    private val _dataFinalizacao = MutableLiveData<Long?>(null)
    val dataFinalizacao: LiveData<Long?> = _dataFinalizacao

    private fun marcadorFinalizacao(idInstituicao: Int) = "jornada_concluida_$idInstituicao"





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

                    _nomeInstituicao.value = instituicao?.nome.orEmpty()

                    _idColeta.value = instituicao?.idColeta

                    Log.d("JORNADA_COLETA", "ID coleta: ${_idColeta.value}")

                    val jornadasAtivas = instituicao
                        ?.jornadas
                        .orEmpty()
                        .filter { it.isAtivo == 1 }

                    if (jornadasAtivas.size == 1) {
                        val jornada = jornadasAtivas.first()
                        jornadaCarregada = jornada
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

                                        val finalizarCarregamento: () -> Unit = {
                                            _etapas.value = montarEtapasJornada(
                                                jornada = jornada,
                                                etapasConcluidas = etapasConcluidas,
                                                possuiVinculo = possuiVinculo
                                            )

                                            calcularProgresso()

                                            if (_progresso.value == 100) {
                                                registrarJornadaConcluida(
                                                    idDoadora = idDoadora,
                                                    idJornada = jornada.id,
                                                    idInstituicao = idInstituicao
                                                )

                                                _estado.value = JornadaEstado.AGUARDANDO_FINALIZACAO
                                            } else {
                                                _estado.value = JornadaEstado.COM_BANCO
                                            }

                                            buscarDocumentosDoadora()

                                            Log.i("Jornada", "Jornada carregada com sucesso.")
                                        }

                                        val etapasEstaticasConcluidas = possuiVinculo && "orientacoes" in etapasConcluidas && "questionario" in etapasConcluidas

                                        if (etapasEstaticasConcluidas) {
                                            garantirProximoVinculo(
                                                jornada = jornada,

                                                onSucesso = finalizarCarregamento,

                                                onErro = { erro ->
                                                    _mensagemErro.value = erro
                                                    _estado.value = JornadaEstado.ERRO
                                                }
                                            )
                                        } else {
                                            finalizarCarregamento()
                                        }
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

    private fun garantirProximoVinculo(
        jornada: JornadaResponse,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        val tag = "JORNADA_PROXIMO_VINCULO"

        val proximaEtapa = jornada.etapas
            .sortedBy { it.ordem }
            .firstOrNull {
                "instituicao_${it.id}" !in etapasInstitucionaisConcluidas
            }

        if (proximaEtapa == null) {
            Log.i(tag, "Todas as etapas foram concluídas.")
            onSucesso()
            return
        }

        val etapaId = "instituicao_${proximaEtapa.id}"

        if (etapaId in _vinculosPorEtapa.value.orEmpty()) {
            Log.d(tag, "A etapa $etapaId já possui vínculo ativo.")
            onSucesso()
            return
        }

        Log.d(tag, "Criando vínculo para a etapa $etapaId")

        criarVinculoEtapa(
            idEtapa = proximaEtapa.id,

            onSucesso = { idVinculo ->
                _vinculosPorEtapa.value =
                    _vinculosPorEtapa.value.orEmpty() +
                            (etapaId to idVinculo)

                associarDocumentosEtapas()

                Log.i(tag, "Novo vínculo criado: $idVinculo")

                onSucesso()
            },

            onErro = { erro ->
                Log.e(tag, erro)
                onErro(erro)
            }
        )
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
            etapasConcluidas + "conheca_doacao"
        } else {
            etapasConcluidas - "conheca_doacao"
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

        val etapasEstaticasConcluidas = idsEstaticos.all {
            it in conclusoesValidas
        }

        val etapasOrdenadas = jornada.etapas
            .sortedBy { it.ordem }

        val primeiraInstitucionalPendente = etapasOrdenadas
            .firstOrNull {
                "instituicao_${it.id}" !in etapasInstitucionaisConcluidas
            }
            ?.id

        val etapasInstituicao = etapasOrdenadas.map { etapa ->
            val etapaId = "instituicao_${etapa.id}"

            val descricaoEtapa = if (etapa.isRepetivel == 1 && etapaId !in etapasInstitucionaisConcluidas) {
                val ciclos = ciclosRestantesPorEtapa[etapa.id]

                when {
                    ciclos == null || ciclos <= 0 ->
                        "${etapa.descricao}\nAguardando a instituição definir seus ciclos de doação."

                    ciclos == 1 -> "${etapa.descricao}\n1 ciclo restante."

                    else -> "${etapa.descricao}\n$ciclos ciclos restantes."
                }
            } else {
                etapa.descricao
            }

            JornadaEtapa(
                id = etapaId,
                titulo = etapa.titulo,
                descricao = descricaoEtapa,
                status = when {
                    etapaId in etapasInstitucionaisConcluidas -> JornadaStatus.CONCLUIDA
                    etapasEstaticasConcluidas && etapa.id == primeiraInstitucionalPendente && etapaId in _vinculosPorEtapa.value.orEmpty() -> JornadaStatus.ATUAL
                    else -> JornadaStatus.BLOQUEADA
                },
                isAgendavel = etapa.isAgendavel,
                isRepetivel = etapa.isRepetivel,
                isObrigatorio = etapa.isObrigatorio,
                isSolicitaArquivo = etapa.isSolicitaArquivo,
                isPrecisaAprovacao = etapa.isPrecisaAprovacao,
                documento = etapa.documento
            )
        }

        return etapasIniciais + etapasInstituicao
    }

    private fun registrarJornadaConcluida(
        idDoadora: Int,
        idJornada: Int,
        idInstituicao: Int
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    jornadaLocalProgressDao.salvarConclusao(
                        JornadaLocalProgress(
                            idDoadora = idDoadora,
                            idJornada = idJornada,
                            idEtapa = marcadorFinalizacao(idInstituicao)
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("Jornada", "Erro ao registrar jornada concluída", e)
            }
        }
    }

    // Jornada deixou de ser listada pela API: se ela chegou ao fim, mostra "finalizada".
    private fun exibirFinalizada(
        idDoadora: Int,
        marcador: JornadaLocalProgress
    ) {
        idDoadoraAtual = idDoadora
        idJornadaAtual = marcador.idJornada

        val idInstituicao = marcador.idEtapa
            .removePrefix("jornada_concluida_")
            .toIntOrNull()

        idInstituicaoSelecionada = idInstituicao
        _dataFinalizacao.value = marcador.dataConclusao
        _estado.value = JornadaEstado.FINALIZADA

        if (idInstituicao != null) {
            MaternaRetrofitFactory(applicationContext)
                .getInstituicaoService()
                .buscarInstituicao(idInstituicao)
                .enqueue(object : Callback<InstituicaoApiResponse> {
                    override fun onResponse(
                        call: Call<InstituicaoApiResponse>,
                        response: Response<InstituicaoApiResponse>
                    ) {
                        _nomeInstituicao.value = response.body()
                            ?.response
                            ?.instituicao
                            ?.firstOrNull()
                            ?.nome
                            .orEmpty()
                    }

                    override fun onFailure(
                        call: Call<InstituicaoApiResponse>,
                        t: Throwable
                    ) = Unit
                })
        }
    }

    // Usado pela rota "jornada" quando a doadora não tem jornada ativa na API.
    fun verificarFinalizadaOuSemBanco(idDoadora: Int) {
        viewModelScope.launch {
            val marcador = try {
                withContext(Dispatchers.IO) {
                    jornadaLocalProgressDao.buscarMarcadorFinalizacao(idDoadora)
                }
            } catch (e: Exception) {
                null
            }

            if (marcador != null) {
                exibirFinalizada(idDoadora, marcador)
            } else {
                exibirSemBanco()
            }
        }
    }

    // Doadora quer começar outra jornada depois de finalizar a anterior.
    fun iniciarNovaJornada(onConcluido: () -> Unit) {
        val idDoadora = idDoadoraAtual
        val idJornada = idJornadaAtual

        if (idDoadora == null || idJornada == null) {
            onConcluido()
            return
        }

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    jornadaLocalProgressDao.limparProgresso(idDoadora, idJornada)
                }
            } catch (e: Exception) {
                Log.e("Jornada", "Erro ao limpar jornada finalizada", e)
            }

            onConcluido()
        }
    }

    fun exibirSemBanco() {
        _mensagemErro.value = ""
        _estado.value = JornadaEstado.SEM_BANCO

        Log.d("JORNADA_DEBUG", "Doadora sem jornada vinculada.")
    }

    fun exibirErro(mensagem: String) {
        _mensagemErro.value = mensagem
        _estado.value = JornadaEstado.ERRO

        Log.e("JORNADA_DEBUG", mensagem)
    }

    fun carregarInstituicao(
        idInstituicao: Int,
        idDoadora: Int
    ) {
        val tag = "JORNADA_DEBUG"

        Log.d(tag, "Iniciando carregamento da jornada.")
        Log.d(tag, "ID Instituição: $idInstituicao")
        Log.d(tag, "ID Doadora: $idDoadora")

        if (idDoadora <= 0) {
            Log.e(tag, "ID da doadora inválido.")

            _mensagemErro.value =
                "Não foi possível identificar a doadora."

            _estado.value = JornadaEstado.ERRO
            return
        }

        if (idInstituicao <= 0) {
            Log.i(tag, "Nenhuma instituição selecionada.")

            _estado.value = JornadaEstado.SEM_BANCO
            return
        }

        idInstituicaoSelecionada = idInstituicao
        idDoadoraAtual = idDoadora

        _estado.value = JornadaEstado.CARREGANDO
        _mensagemErro.value = ""

        Log.d(tag, "Consultando instituição...")

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

                        etapasInstitucionaisConcluidas = emptySet()
                        ciclosRestantesPorEtapa = emptyMap()
                        _vinculosPorEtapa.value = emptyMap()
                        associarDocumentosEtapas()

                        viewModelScope.launch {
                            val marcador = withContext(Dispatchers.IO) {
                                jornadaLocalProgressDao
                                    .buscarMarcadorFinalizacaoDaJornada(idDoadora, idJornada)
                            }

                            if (marcador != null) {
                                exibirFinalizada(idDoadora, marcador)
                            } else {
                                onResultado(false)
                            }
                        }
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
                        "IDs esperados: jornada=$idJornada, instituicao=$idInstituicao, etapa=$idEtapa"
                    )

                    response.body()?.response?.jornadas?.forEach { jornada ->
                        Log.d(
                            "JornadaVinculo",
                            "Jornada recebida: ${jornada.id} | Instituição: ${jornada.instituicao.id}"
                        )

                        jornada.etapas.forEach { etapa ->
                            Log.d(
                                "JornadaVinculo",
                                "Etapa recebida: ${etapa.id} | Vínculos: ${etapa.vinculos.size}"
                            )

                            etapa.vinculos.forEach { vinculo ->
                                Log.d(
                                    "JornadaVinculo",
                                    """
                                            ==============================
                                            Jornada: ${jornada.id}
                                            Instituição: ${jornada.instituicao.id}
                                            Etapa: ${etapa.id}
                                            ID do vínculo: ${vinculo.id}
                                            Ciclo: ${vinculo.ciclo}
                                            Status: ${vinculo.status}
                                            Encerrado: ${vinculo.encerrado}
                                            Data de conclusão: ${vinculo.dataConclusao}
                                            ==============================
                                            """.trimIndent()
                                )
                            }
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

                    val etapasDaJornada = response.body()
                        ?.response
                        ?.jornadas
                        .orEmpty()
                        .firstOrNull {
                            it.id == idJornada &&
                                    it.instituicao.id == idInstituicao
                        }
                        ?.etapas
                        .orEmpty()

                    etapasInstitucionaisConcluidas = etapasDaJornada
                        .filter { etapa ->
                            val repetivel = jornadaCarregada?.etapas?.any {
                                it.id == etapa.id && it.isRepetivel == 1
                            } == true

                            if (repetivel) {
                                // Etapa repetível só é definitiva quando todos os ciclos foram concluídos
                                etapa.vinculos.any { it.encerrado == 1 }
                            } else {
                                jornadaCarregada?.etapas?.any {
                                    it.id == etapa.id && it.isRepetivel == 0
                                } == true &&
                                        etapa.vinculos.any { vinculo ->
                                            vinculo.status == "concluida" &&
                                                    !vinculo.dataConclusao.isNullOrBlank()
                                        }
                            }
                        }
                        .map { etapa ->
                            "instituicao_${etapa.id}"
                        }
                        .toSet()

                    // Ciclos restantes (a instituição define e conclui cada ciclo)
                    ciclosRestantesPorEtapa = etapasDaJornada
                        .filter { etapa ->
                            jornadaCarregada?.etapas?.any {
                                it.id == etapa.id && it.isRepetivel == 1
                            } == true
                        }
                        .mapNotNull { etapa ->
                            etapa.vinculos
                                .filter { it.encerrado == 0 }
                                .maxByOrNull { it.id }
                                ?.let { etapa.id to it.ciclo }
                        }
                        .toMap()

                    Log.d(
                        "JornadaVinculo",
                        "Etapas institucionais concluídas: $etapasInstitucionaisConcluidas"
                    )

                    val vinculos = response.body()
                        ?.response
                        ?.jornadas
                        .orEmpty()
                        .firstOrNull {
                            it.id == idJornada &&
                                    it.instituicao.id == idInstituicao
                        }
                        ?.etapas
                        .orEmpty()
                        .mapNotNull { etapa ->

                            val vinculoAtivo = etapa.vinculos
                                .filter { isVinculoAtivo(it) }
                                .maxByOrNull { it.id }

                            vinculoAtivo?.let {
                                "instituicao_${etapa.id}" to it.id
                            }
                        }
                        .toMap()

                    _vinculosPorEtapa.value = vinculos

                    associarDocumentosEtapas()

                    Log.d(
                        "DOCUMENTOS_API",
                        "Vínculos por etapa: $vinculos"
                    )

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
        if (
            _cancelandoJornada.value == true ||
            _jornadaCancelada.value == true
        ) return

        if (cancelamentoConfirmadoApi) {
            _cancelandoJornada.value = true
            _erroCancelamento.value = null

            finalizarCancelamentoLocal()
            return
        }

        val idInstituicao = idInstituicaoSelecionada ?: run {
            _estado.value = JornadaEstado.SEM_BANCO
            return
        }

        if (_estado.value == JornadaEstado.CARREGANDO) return

        _estado.value = JornadaEstado.CARREGANDO
        _mensagemErro.value = ""

        testarBuscaInstituicao(idInstituicao)
    }

    private val _documentosEnviados = MutableLiveData<Map<String, String>>(emptyMap())
    val documentosEnviados: LiveData<Map<String, String>> = _documentosEnviados

    private val _documentosDoadora = MutableLiveData<List<DocumentoDoadoraResponse>>(emptyList())
    val documentosDoadora: LiveData<List<DocumentoDoadoraResponse>> = _documentosDoadora

    private val _vinculosPorEtapa = MutableLiveData<Map<String, Int>>(emptyMap())
    val vinculosPorEtapa: LiveData<Map<String, Int>> = _vinculosPorEtapa

    private val _documentosPorEtapa = MutableLiveData<Map<String, DocumentoDoadoraResponse>>(emptyMap())
    val documentosPorEtapa: LiveData<Map<String, DocumentoDoadoraResponse>> = _documentosPorEtapa

    private val _estadoDocumentos = MutableLiveData(DocumentoEstado.INICIAL)
    val estadoDocumentos: LiveData<DocumentoEstado> = _estadoDocumentos

    private val _documentosEmRegistro = MutableLiveData<Set<String>>(emptySet())
    val documentosEmRegistro: LiveData<Set<String>> = _documentosEmRegistro

    private val _erroDocumentos = MutableLiveData<String?>(null)
    val erroDocumentos: LiveData<String?> = _erroDocumentos

    private val _errosRegistroDocumento = MutableLiveData<Map<String, String>>(emptyMap())
    val errosRegistroDocumento: LiveData<Map<String, String>> = _errosRegistroDocumento

    private var consultaDocumentosAtual: Call<DocumentosDoadoraResponse>? = null

    fun registrarDocumentoEnviado(
        etapaId: String,
        url: String
    ) {
        if (
            _cancelandoJornada.value == true ||
            _jornadaCancelada.value == true
        ) return

        if (etapaId in _documentosEmRegistro.value.orEmpty()) {
            Log.w("DocumentoEnvio", "Registro já em andamento.")
            return
        }

        val idEtapa = obterIdEtapaReal(etapaId)

        val etapa = _etapas.value?.firstOrNull {
            it.id == etapaId
        }

        if (
            idEtapa == null ||
            etapa == null ||
            etapa.status != JornadaStatus.ATUAL ||
            etapa.isSolicitaArquivo != 1
        ) {
            finalizarRegistroDocumento(
                etapaId,
                "Esta etapa não permite o envio de documentos."
            )
            return
        }

        if (_estadoDocumentos.value != DocumentoEstado.SUCESSO) {
            finalizarRegistroDocumento(
                etapaId,
                "Aguarde a consulta dos documentos."
            )
            return
        }

        val possuiDocumentoRegistrado = _documentosPorEtapa.value.orEmpty().containsKey(etapaId) || _documentosEnviados.value.orEmpty().containsKey(etapaId)

        if (possuiDocumentoRegistrado) {
            finalizarRegistroDocumento(
                etapaId,
                "Esta etapa já possui um documento registrado."
            )
            return
        }

        iniciarRegistroDocumento(etapaId)

        obterVinculoEtapa(
            idEtapa = idEtapa,

            onSucesso = { idVinculo ->

                if (idVinculo != null) {
                    enviarDocumentoMaterna(
                        idVinculo = idVinculo,
                        url = url,
                        onSucesso = { documento ->
                            _documentosEnviados.value = _documentosEnviados.value.orEmpty() + (etapaId to documento.documento)
                            _vinculosPorEtapa.value = _vinculosPorEtapa.value.orEmpty() + (etapaId to idVinculo)

                            buscarDocumentosDoadora()
                            finalizarRegistroDocumento(etapaId)

                            if (
                                etapa.isPrecisaAprovacao == 0 &&
                                documento.idStatusDocumento == 2
                            ) {
                                avancarEtapaDocumento(etapaId)
                            }

                            Log.d(
                                "DocumentoEnvio",
                                "Documento registrado na API Materna."
                            )
                        },
                        onErro = { erro ->
                            buscarDocumentosDoadora()

                            finalizarRegistroDocumento(
                                etapaId,
                                erro
                            )
                        }
                    )
                } else {
                    finalizarRegistroDocumento(
                        etapaId,
                        "Nenhum vínculo ativo encontrado."
                    )
                }
            },

            onErro = { erro ->
                finalizarRegistroDocumento(
                    etapaId,
                    erro
                )
            }
        )
    }

    private fun obterIdEtapaReal(etapaId: String): Int? {
        if (!etapaId.startsWith("instituicao_")) {
            return null
        }

        return etapaId.removePrefix("instituicao_").toIntOrNull()
    }

    private fun isVinculoAtivo(vinculo: VinculoEtapa): Boolean {
        return vinculo.encerrado == 0 &&
                vinculo.status == "em_andamento" &&
                vinculo.dataConclusao.isNullOrBlank()
    }

    private fun obterVinculoEtapa(
        idEtapa: Int,
        onSucesso: (Int?) -> Unit,
        onErro: (String) -> Unit,
        // Chamado quando a API informa que a doadora não tem mais nenhuma jornada ativa (404)
        onSemJornadaAtiva: (() -> Unit)? = null
    ) {
        val idDoadora = idDoadoraAtual
        val idJornada = idJornadaAtual
        val idInstituicao = idInstituicaoSelecionada

        if (
            idDoadora == null ||
            idJornada == null ||
            idInstituicao == null
        ) {
            onErro("Não foi possível identificar a jornada.")
            return
        }

        val service = MaternaRetrofitFactory(applicationContext)
            .getVinculoEtapaDoadoraService()

        service.buscarVinculosDoadora(idDoadora)
            .enqueue(object : Callback<VinculoEtapaDoadoraResponse> {

                override fun onResponse(
                    call: Call<VinculoEtapaDoadoraResponse>,
                    response: Response<VinculoEtapaDoadoraResponse>
                ) {
                    if (
                        !response.isSuccessful ||
                        response.body()?.status != true
                    ) {
                        Log.e(
                            "DocumentoEnvio",
                            "Erro ao buscar vínculo: HTTP ${response.code()}"
                        )

                        if (response.code() == 404 && onSemJornadaAtiva != null) {
                            onSemJornadaAtiva()
                            return
                        }

                        onErro("Não foi possível consultar os vínculos (HTTP ${response.code()}).")
                        return
                    }

                    val vinculo = response.body()
                        ?.response
                        ?.jornadas
                        .orEmpty()
                        .firstOrNull {
                            it.id == idJornada && it.instituicao.id == idInstituicao
                        }
                        ?.etapas
                        ?.firstOrNull { it.id == idEtapa }
                        ?.vinculos
                        .orEmpty()
                        .filter { isVinculoAtivo(it) }
                        .maxByOrNull { it.id }

                    if (vinculo == null) {
                        Log.w(
                            "DocumentoEnvio",
                            "Etapa $idEtapa sem vínculo ativo."
                        )

                        onSucesso(null)
                        return
                    }

                    Log.d(
                        "DocumentoEnvio",
                        "Etapa: $idEtapa | Vínculo: ${vinculo.id}"
                    )

                    onSucesso(vinculo.id)
                }

                override fun onFailure(
                    call: Call<VinculoEtapaDoadoraResponse>,
                    t: Throwable
                ) {
                    Log.e("DocumentoEnvio", "Falha ao buscar vínculo", t)

                    onErro("Falha de conexão ao buscar vínculo.")
                }
            })
    }

    private fun criarVinculoEtapa(
        idEtapa: Int,
        onSucesso: (Int) -> Unit,
        onErro: (String) -> Unit
    ) {
        val idDoadora = idDoadoraAtual
        val idJornada = idJornadaAtual
        val idInstituicao = idInstituicaoSelecionada

        if (
            idDoadora == null ||
            idJornada == null ||
            idInstituicao == null
        ) {
            onErro("Dados da jornada incompletos.")
            return
        }

        if (criandoVinculo) {
            onErro("Aguarde a criação do vínculo.")
            return
        }

        criandoVinculo = true

        val request = CriarVinculoEtapaRequest(
            idDoadora = idDoadora,
            idEtapa = idEtapa,
            idInstituicao = idInstituicao,
            idJornada = idJornada
        )

        MaternaRetrofitFactory(applicationContext)
            .getVinculoEtapaDoadoraService()
            .criarVinculoEtapa(request)
            .enqueue(object : Callback<CriarVinculoEtapaResponse> {

                override fun onResponse(
                    call: Call<CriarVinculoEtapaResponse>,
                    response: Response<CriarVinculoEtapaResponse>
                ) {
                    criandoVinculo = false

                    val idVinculo = response.body()?.response?.id

                    if (
                        response.isSuccessful &&
                        response.body()?.status == true &&
                        idVinculo != null
                    ) {
                        Log.d(
                            "DocumentoEnvio",
                            "Vínculo criado: $idVinculo"
                        )

                        onSucesso(idVinculo)
                    } else {
                        Log.e(
                            "DocumentoEnvio",
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

                    Log.e(
                        "DocumentoEnvio",
                        "Falha ao criar vínculo",
                        t
                    )

                    onErro("Falha de conexão ao criar vínculo.")
                }
            })
    }

    private fun enviarDocumentoMaterna(
        idVinculo: Int,
        url: String,
        onSucesso: (DocumentoResponse) -> Unit,
        onErro: (String) -> Unit
    ) {
        val idDoadora = idDoadoraAtual

        if (idDoadora == null) {
            onErro("Doadora não identificada.")
            return
        }

        val request = EnviarDocumentoRequest(
            idDoadora = idDoadora,
            documento = url
        )

        MaternaRetrofitFactory(applicationContext)
            .getDocumentoService()
            .enviarDocumento(idVinculo, request)
            .enqueue(object : Callback<EnviarDocumentoResponse> {

                override fun onResponse(
                    call: Call<EnviarDocumentoResponse>,
                    response: Response<EnviarDocumentoResponse>
                ) {
                    Log.d("DocumentoEnvio", "HTTP: ${response.code()}")

                    val retorno = response.body()
                    val documento = retorno?.response

                    if (
                        response.isSuccessful &&
                        retorno?.status == true &&
                        documento != null
                    ) {
                        Log.d("DocumentoEnvio", "ID do documento: ${documento.idDocumento}")
                        Log.d("DocumentoEnvio", "ID do status: ${documento.idStatusDocumento}")
                        Log.d("DocumentoEnvio", "Status: ${documento.status}")
                        Log.d("DocumentoEnvio", "Mensagem: ${retorno.message}")

                        onSucesso(documento)
                    } else {
                        Log.e("DocumentoEnvio", "Falha HTTP ${response.code()}")

                        onErro(
                            retorno?.message ?: "Não foi possível registrar o documento."
                        )
                    }
                }

                override fun onFailure(
                    call: Call<EnviarDocumentoResponse>,
                    t: Throwable
                ) {
                    Log.e("DocumentoEnvio", "Falha na requisição: ${t.javaClass.simpleName}")

                    onErro("Falha na comunicação com a API Materna.")
                }
            })
    }

    private fun avancarEtapaDocumento(etapaId: String) {
        val tag = "JORNADA_DOCUMENTO"

        val etapa = _etapas.value.orEmpty()
            .firstOrNull { it.id == etapaId }

        if (
            etapa == null ||
            etapa.status != JornadaStatus.ATUAL ||
            etapa.isSolicitaArquivo != 1 ||
            etapa.isRepetivel != 0 ||
            etapa.isPrecisaAprovacao != 0
        ) {
            Log.w(tag, "Etapa não elegível para avanço automático.")
            return
        }

        concluirEtapaInstitucional(etapaId)
    }

    private fun atualizarStatusEtapas(etapaId: String) {
        val etapas = _etapas.value.orEmpty()

        val indiceAtual = etapas.indexOfFirst {
            it.id == etapaId
        }

        if (indiceAtual == -1) return

        if (etapas[indiceAtual].status != JornadaStatus.ATUAL) {
            return
        }

        _etapas.value = etapas.mapIndexed { index, etapa ->
            when {
                index == indiceAtual -> {
                    etapa.copy(
                        status = JornadaStatus.CONCLUIDA
                    )
                }

                index == indiceAtual + 1 &&
                        etapa.status == JornadaStatus.BLOQUEADA -> {
                    etapa.copy(
                        status = JornadaStatus.ATUAL
                    )
                }

                else -> etapa
            }
        }

        calcularProgresso()

        Log.d("JornadaVinculo", "Etapa concluída: $etapaId")
    }

    private fun encerrarVinculoEtapa(
        idVinculo: Int,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        val tag = "JORNADA_CONCLUSAO_API"

        val request = ConcluirEtapaRequest(
            dataConclusao = LocalDate.now().toString()
        )

        Log.d(tag, "Encerrando vínculo: $idVinculo")
        Log.d(tag, "Data de conclusão: ${request.dataConclusao}")

        MaternaRetrofitFactory(applicationContext)
            .getVinculoEtapaDoadoraService()
            .concluirEtapa(idVinculo, request)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d(tag, "HTTP: ${response.code()}")
                    Log.d(tag, "URL: ${call.request().url}")

                    if (response.isSuccessful) {
                        val retorno = response.body()?.string()

                        Log.d(tag, "Resposta: $retorno")

                        onSucesso()
                    } else {
                        val erro = response.errorBody()?.string()

                        Log.e(tag, "Erro: $erro")

                        onErro(
                            "Não foi possível concluir a etapa."
                        )
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {
                    Log.e(tag, "Falha na requisição", t)

                    onErro(
                        "Falha de conexão ao concluir a etapa."
                    )
                }
            })
    }

    private fun concluirEtapaInstitucional(
        etapaId: String
    ) {
        if (
            _cancelandoJornada.value == true ||
            _jornadaCancelada.value == true
        ) return

        val tag = "JORNADA_CONCLUSAO"

        if (concluindoEtapa) {
            Log.w(tag, "Conclusão já em andamento.")
            return
        }

        val etapa = _etapas.value.orEmpty()
            .firstOrNull { it.id == etapaId }

        val idEtapa = obterIdEtapaReal(etapaId)

        if (
            etapa == null ||
            idEtapa == null ||
            etapa.status != JornadaStatus.ATUAL ||
            etapa.isRepetivel != 0
        ) {
            Log.w(tag, "Etapa não elegível para conclusão.")
            return
        }

        concluindoEtapa = true

        obterVinculoEtapa(
            idEtapa = idEtapa,

            onSucesso = { idVinculo ->
                if (idVinculo == null) {
                    concluindoEtapa = false

                    Log.w(tag, "Nenhum vínculo ativo encontrado.")

                    recarregarJornada()
                } else {
                    encerrarVinculoEtapa(
                        idVinculo = idVinculo,

                        onSucesso = {
                            concluindoEtapa = false

                            Log.i(tag, "Etapa concluída: $etapaId")

                            recarregarJornada()
                        },

                        onErro = { erro ->
                            concluindoEtapa = false

                            Log.e(tag, erro)
                        }
                    )
                }
            },

            onErro = { erro ->
                concluindoEtapa = false

                Log.e(tag, erro)
            }
        )
    }

    fun concluirEtapaSimples(etapaId: String) {
        val tag = "JORNADA_ETAPA_SIMPLES"

        val etapa = _etapas.value.orEmpty()
            .firstOrNull { it.id == etapaId }

        if (etapa == null) {
            Log.e(tag, "Etapa não encontrada.")
            return
        }

        val isEtapaSimples =
            etapa.id.startsWith("instituicao_") &&
                    etapa.status == JornadaStatus.ATUAL &&
                    etapa.isAgendavel == 0 &&
                    etapa.isSolicitaArquivo == 0 &&
                    etapa.isPrecisaAprovacao == 0 &&
                    etapa.isRepetivel == 0 &&
                    etapa.isObrigatorio == 1

        if (!isEtapaSimples) {
            Log.w(tag, "Etapa não elegível para conclusão simples.")
            return
        }

        concluirEtapaInstitucional(etapaId)
    }

    private fun recuperarConclusaoDocumento() {
        if (
            _cancelandoJornada.value == true ||
            _jornadaCancelada.value == true
        ) return

        val tag = "JORNADA_RECUPERACAO"

        if (
            concluindoEtapa ||
            _estado.value != JornadaEstado.COM_BANCO ||
            _estadoDocumentos.value != DocumentoEstado.SUCESSO ||
            _documentosEmRegistro.value.orEmpty().isNotEmpty()
        ) {
            return
        }

        val etapaAtual = _etapas.value.orEmpty()
            .firstOrNull {
                it.status == JornadaStatus.ATUAL
            } ?: return

        if (
            !etapaAtual.id.startsWith("instituicao_") ||
            etapaAtual.isSolicitaArquivo != 1 ||
            etapaAtual.isPrecisaAprovacao != 0 ||
            etapaAtual.isRepetivel != 0 ||
            etapaAtual.isAgendavel != 0
        ) {
            return
        }

        val documento = _documentosPorEtapa.value
            .orEmpty()[etapaAtual.id] ?: return

        val idVinculo = _vinculosPorEtapa.value
            .orEmpty()[etapaAtual.id] ?: return

        if (
            documento.idStatusDocumento != 2 ||
            documento.idJornadaDoacao != idVinculo
        ) {
            return
        }

        Log.i(
            tag,
            "Recuperando conclusão da etapa ${etapaAtual.id}"
        )

        avancarEtapaDocumento(etapaAtual.id)
    }

    fun buscarDocumentosDoadora() {
        if (
            _cancelandoJornada.value == true ||
            _jornadaCancelada.value == true
        ) return

        val tag = "DOCUMENTOS_API"

        val consultaAnterior = consultaDocumentosAtual

        val novaConsulta = MaternaRetrofitFactory(applicationContext)
            .getDocumentoService()
            .buscarDocumentosDoadora()

        consultaDocumentosAtual = novaConsulta

        consultaAnterior?.cancel()

        _estadoDocumentos.value = DocumentoEstado.CARREGANDO
        _erroDocumentos.value = null

        Log.d(tag, "Buscando documentos da doadora...")

        novaConsulta.enqueue(object : Callback<DocumentosDoadoraResponse> {

                override fun onResponse(
                    call: Call<DocumentosDoadoraResponse>,
                    response: Response<DocumentosDoadoraResponse>
                ) {
                    if (call !== consultaDocumentosAtual || call.isCanceled) {
                        return
                    }

                    consultaDocumentosAtual = null

                    val retorno = response.body()
                    val erroApi = response.errorBody()?.string().orEmpty()

                    val semDocumentos =
                        response.code() == 404 &&
                                erroApi.contains(
                                    "\"status_code\"\\s*:\\s*404".toRegex()
                                ) &&
                                erroApi.contains("Não foram encontrados dados")

                    Log.d(tag, "HTTP: ${response.code()}")

                    if (semDocumentos) {
                        _documentosDoadora.value = emptyList()

                        associarDocumentosEtapas()

                        _erroDocumentos.value = null
                        _estadoDocumentos.value = DocumentoEstado.SUCESSO

                        Log.i(
                            tag,
                            "Nenhum documento encontrado. Envio liberado."
                        )

                        return
                    }

                    if (
                        response.isSuccessful &&
                        retorno?.status == true &&
                        retorno.response != null
                    ) {
                        val documentos = retorno.response.orEmpty()

                        _documentosDoadora.value = documentos

                        associarDocumentosEtapas()

                        _estadoDocumentos.value = DocumentoEstado.SUCESSO
                        recuperarConclusaoDocumento()

                        Log.d(
                            tag,
                            "Documentos encontrados: ${documentos.size}"
                        )

                        documentos.forEach { documento ->
                            Log.d(
                                tag,
                                "ID: ${documento.idDocumento} | " +
                                        "Vínculo: ${documento.idJornadaDoacao} | " +
                                        "ID Status: ${documento.idStatusDocumento} | " +
                                        "Status: ${documento.status}"
                            )
                        }

                    } else {
                        _erroDocumentos.value =
                            "Não foi possível consultar os documentos."

                        _estadoDocumentos.value =
                            DocumentoEstado.ERRO

                        Log.e(
                            tag,
                            "Erro ao buscar documentos: HTTP ${response.code()}"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<DocumentosDoadoraResponse>,
                    t: Throwable
                ) {
                    if (call !== consultaDocumentosAtual || call.isCanceled) {
                        return
                    }

                    consultaDocumentosAtual = null

                    _erroDocumentos.value = "Não foi possível conectar ao servidor."

                    _estadoDocumentos.value = DocumentoEstado.ERRO

                    Log.e(tag, "Falha ao buscar documentos", t)
                }
            })
    }

    private fun associarDocumentosEtapas() {
        val vinculos = _vinculosPorEtapa.value.orEmpty()
        val documentos = _documentosDoadora.value.orEmpty()

        val documentosPorEtapa = vinculos
            .mapNotNull { (etapaId, idVinculo) ->

                val documentoAtual = documentos
                    .filter { it.idJornadaDoacao == idVinculo }
                    .maxByOrNull { it.idDocumento }

                documentoAtual?.let {
                    etapaId to it
                }
            }
            .toMap()

        _documentosPorEtapa.value = documentosPorEtapa

        documentosPorEtapa.forEach { (etapaId, documento) ->
            Log.d(
                "DOCUMENTOS_API",
                "Etapa: $etapaId | Documento: ${documento.idDocumento} | Status: ${documento.status}"
            )
        }
    }

    private fun iniciarRegistroDocumento(etapaId: String) {
        _documentosEmRegistro.value = _documentosEmRegistro.value.orEmpty() + etapaId
        _errosRegistroDocumento.value = _errosRegistroDocumento.value.orEmpty() - etapaId
    }

    private fun finalizarRegistroDocumento(
        etapaId: String,
        erro: String? = null
    ) {
        _documentosEmRegistro.value = _documentosEmRegistro.value.orEmpty() - etapaId

        if (erro != null) {
            _errosRegistroDocumento.value = _errosRegistroDocumento.value.orEmpty() + (etapaId to erro)
            Log.e("DocumentoEnvio", erro)
        }
    }

    fun pularEtapaOpcional(etapaId: String) {
        val tag = "JORNADA_ETAPA_OPCIONAL"

        val etapa = _etapas.value.orEmpty()
            .firstOrNull { it.id == etapaId }

        if (
            etapa == null ||
            !etapa.id.startsWith("instituicao_") ||
            etapa.status != JornadaStatus.ATUAL ||
            etapa.isObrigatorio != 0 ||
            etapa.isRepetivel != 0
        ) {
            Log.w(tag, "Esta etapa não pode ser pulada.")
            return
        }

        Log.d(tag, "Pulando etapa: $etapaId")

        concluirEtapaInstitucional(etapaId)
    }

    fun cancelarJornada() {
        if (
            _cancelandoJornada.value == true ||
            _jornadaCancelada.value == true
        ) return

        val tag = "JORNADA_CANCELAMENTO"

        if (_cancelandoJornada.value == true) return

        _erroCancelamento.value = null

        if (
            _estado.value != JornadaEstado.COM_BANCO ||
            concluindoEtapa ||
            criandoVinculo ||
            _documentosEmRegistro.value.orEmpty().isNotEmpty() ||
            _estadoDocumentos.value == DocumentoEstado.CARREGANDO
        ) {
            _erroCancelamento.value =
                "Aguarde a operação atual ser concluída."
            return
        }

        val idDoadora = idDoadoraAtual

        val etapaAtual = _etapas.value.orEmpty()
            .firstOrNull {
                it.status == JornadaStatus.ATUAL &&
                        it.id.startsWith("instituicao_")
            }

        val idEtapa = etapaAtual?.id?.let {
            obterIdEtapaReal(it)
        } ?: idPrimeiraEtapaReal

        if (idDoadora == null || idEtapa == null) {
            _erroCancelamento.value =
                "Não foi possível identificar a jornada."

            Log.e(tag, "Dados da jornada incompletos.")
            return
        }

        _cancelandoJornada.value = true

        Log.d(tag, "Buscando vínculo da etapa: $idEtapa")

        obterVinculoEtapa(
            idEtapa = idEtapa,

            onSucesso = { idVinculo ->
                if (idVinculo == null) {
                    _cancelandoJornada.value = false
                    _erroCancelamento.value =
                        "Nenhum vínculo ativo encontrado."

                    Log.e(tag, "Vínculo não encontrado.")
                } else {
                    solicitarCancelamentoJornada(
                        idVinculo = idVinculo,
                        idDoadora = idDoadora
                    )
                }

                Log.d(tag, "Vínculo recuperado: $idVinculo")
            },

            onErro = { erro ->
                _cancelandoJornada.value = false
                _erroCancelamento.value = erro

                Log.e(tag, erro)
            },

            // A jornada já foi encerrada no servidor: só falta limpar o progresso local
            onSemJornadaAtiva = {
                Log.w(tag, "Nenhuma jornada ativa no servidor. Limpando progresso local.")

                cancelamentoConfirmadoApi = true
                finalizarCancelamentoLocal()
            }
        )
    }

    private fun solicitarCancelamentoJornada(
        idVinculo: Int,
        idDoadora: Int
    ) {
        val tag = "JORNADA_CANCELAMENTO"

        val request = CancelarJornadaRequest(
            idDoadora = idDoadora
        )

        Log.d(tag, "Iniciando cancelamento da jornada.")
        Log.d(tag, "ID do vínculo: $idVinculo")

        MaternaRetrofitFactory(applicationContext)
            .getVinculoEtapaDoadoraService()
            .cancelarJornada(idVinculo, request)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d(tag, "HTTP: ${response.code()}")
                    Log.d(tag, "URL: ${call.request().url}")

                    if (response.isSuccessful) {
                        val retorno = response.body()?.string()

                        Log.i(tag, "Resposta: $retorno")
                        Log.i(tag, "Jornada cancelada com sucesso.")

                        cancelamentoConfirmadoApi = true

                        finalizarCancelamentoLocal()
                    } else {
                        val erro = response.errorBody()?.string()

                        Log.e(tag, "Erro: $erro")

                        _cancelandoJornada.value = false
                        _erroCancelamento.value = "Não foi possível cancelar a jornada."
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {
                    _cancelandoJornada.value = false

                    _erroCancelamento.value =
                        "Falha de conexão ao cancelar a jornada."

                    Log.e(tag, "Falha na requisição", t)
                }
            })
    }

    private fun finalizarCancelamentoLocal() {
        val tag = "JORNADA_CANCELAMENTO"

        val idDoadora = idDoadoraAtual
        val idJornada = idJornadaAtual

        if (idDoadora == null || idJornada == null) {
            _cancelandoJornada.value = false
            _erroCancelamento.value =
                "Não foi possível identificar o progresso local."

            Log.e(tag, "Identificadores incompletos.")
            return
        }

        viewModelScope.launch {
            try {
                val registrosExcluidos = withContext(Dispatchers.IO) {
                    jornadaLocalProgressDao.limparProgresso(
                        idDoadora = idDoadora,
                        idJornada = idJornada
                    )
                }

                Log.i(
                    tag,
                    "Registros locais excluídos: $registrosExcluidos"
                )

                _jornadaCancelada.value = true

            } catch (e: Exception) {
                Log.e(tag, "Erro ao limpar progresso local.", e)

                _erroCancelamento.value =
                    "Jornada cancelada no servidor, mas não foi possível limpar o progresso local."
            } finally {
                _cancelandoJornada.value = false
            }
        }
    }

    fun confirmarCancelamentoJornada() {
        if (_jornadaCancelada.value != true) return
        if (_cancelandoJornada.value == true) return

        val tag = "JORNADA_CANCELAMENTO"

        val consulta = consultaDocumentosAtual
        consultaDocumentosAtual = null
        consulta?.cancel()

        // Identificadores da jornada
        idInstituicaoSelecionada = null
        idJornadaAtual = null
        idPrimeiraEtapaReal = null
        jornadaCarregada = null

        // Progresso
        etapasInstitucionaisConcluidas = emptySet()
        _etapas.value = emptyList()
        _progresso.value = 0
        _nomeInstituicao.value = ""
        _idColeta.value = null

        // Vínculos e documentos
        _vinculosPorEtapa.value = emptyMap()
        _documentosEnviados.value = emptyMap()
        _documentosDoadora.value = emptyList()
        _documentosPorEtapa.value = emptyMap()
        _documentosEmRegistro.value = emptySet()
        _errosRegistroDocumento.value = emptyMap()
        _estadoDocumentos.value = DocumentoEstado.INICIAL
        _erroDocumentos.value = null

        // Controle de operações
        criandoVinculo = false
        concluindoEtapa = false
        cancelamentoConfirmadoApi = false

        // Estados da interface
        _mensagemErro.value = ""
        _erroCancelamento.value = null
        _cancelandoJornada.value = false
        _jornadaCancelada.value = false
        _estado.value = JornadaEstado.SEM_BANCO

        Log.i(tag, "Estados locais da jornada limpos.")
    }

    override fun onCleared() {
        val consulta = consultaDocumentosAtual
        consultaDocumentosAtual = null
        consulta?.cancel()

        super.onCleared()
    }
}