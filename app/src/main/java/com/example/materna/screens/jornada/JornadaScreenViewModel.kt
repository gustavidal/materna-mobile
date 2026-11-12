package com.example.materna.screens.jornada

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materna.models.domain.JornadaEtapa
import com.example.materna.models.domain.JornadaStatus
import com.example.materna.models.responses.InstituicaoApiResponse
import com.example.materna.models.responses.JornadaResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
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

class JornadaScreenViewModel : ViewModel() {
    private val _estado = MutableLiveData<JornadaEstado>()
    val estado: LiveData<JornadaEstado> = _estado

    private val _etapas = MutableLiveData<List<JornadaEtapa>>()
    val etapas: LiveData<List<JornadaEtapa>> = _etapas

    private val _progresso = MutableLiveData<Int>()
    val progresso: LiveData<Int> = _progresso

    private val _mensagemErro = MutableLiveData<String>()
    val mensagemErro: LiveData<String> = _mensagemErro

    private fun carregarJornadaSimulada() {
        // Simulação: a doadora ainda não selecionou um banco.
        val bancoSelecionado = true

        if (!bancoSelecionado) {
            _estado.value = JornadaEstado.SEM_BANCO
            return
        }

        _etapas.value = listOf(
            JornadaEtapa(
                id = "conheca_doacao",
                titulo = "Conheça a doação",
                descricao = "Entenda o impacto",
                status = JornadaStatus.CONCLUIDA
            ),
            JornadaEtapa(
                id = "orientacoes",
                titulo = "Orientações",
                descricao = "Higiene e cuidados",
                status = JornadaStatus.CONCLUIDA
            ),
            JornadaEtapa(
                id = "questionario",
                titulo = "Questionário inicial",
                descricao = "Triagem informativa",
                status = JornadaStatus.CONCLUIDA
            ),
            JornadaEtapa(
                id = "documentos",
                titulo = "Envio de documentos",
                descricao = "Exames pré-natais e comprovantes",
                status = JornadaStatus.ATUAL
            ),
            JornadaEtapa(
                id = "agendamento",
                titulo = "Agendamento de coleta",
                descricao = "Escolha data e horário",
                status = JornadaStatus.BLOQUEADA
            )
        )

        calcularProgresso()

        _estado.value = JornadaEstado.COM_BANCO
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
        val call = MaternaRetrofitFactory().getInstituicaoService()
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
                        _etapas.value = montarEtapasJornada(jornada)

                        calcularProgresso()

                        _estado.value = JornadaEstado.COM_BANCO
                        Log.i("Jornada", "Timeline carregada: ${_etapas.value?.size} etapas")
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
                    Log.e("Jornada", "Erro HTTP ${response.code()}: ${response.body()?.message}")
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
        jornada: JornadaResponse
    ): List<JornadaEtapa> {

        val etapasIniciais = listOf(
            JornadaEtapa(
                id = "conheca_doacao",
                titulo = "Conheça a doação",
                descricao = "Entenda o impacto da doação",
                status = JornadaStatus.ATUAL
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
        )

        val etapasInstituicao = jornada.etapas
            .sortedBy { it.ordem }
            .mapIndexed { index, etapa ->
                JornadaEtapa(
                    id = "instituicao_${etapa.id}",
                    titulo = etapa.titulo,
                    descricao = etapa.descricao,
                    status = if (index == 0 && etapasIniciais[2].status == JornadaStatus.CONCLUIDA) {
                        JornadaStatus.ATUAL
                    } else {
                        JornadaStatus.BLOQUEADA
                    }
                )
            }

        val etapaFinal = JornadaEtapa(
            id = "agendamento_coleta",
            titulo = "Agendamento de coleta",
            descricao = "Agende a coleta do seu leite",
            status = JornadaStatus.BLOQUEADA
        )

        return etapasIniciais + etapasInstituicao + etapaFinal
    }

    private var idInstituicaoSelecionada: Int? = null

    fun carregarInstituicao(idInstituicao: Int) {
        if (idInstituicao <= 0) return

        if (
            idInstituicaoSelecionada == idInstituicao &&
            _estado.value == JornadaEstado.COM_BANCO
        ) return

        idInstituicaoSelecionada = idInstituicao

        _estado.value = JornadaEstado.CARREGANDO
        _mensagemErro.value = ""

        testarBuscaInstituicao(idInstituicao)
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