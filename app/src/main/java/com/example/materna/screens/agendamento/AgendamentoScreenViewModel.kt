package com.example.materna.screens.agendamento

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materna.models.requests.AgendamentoRequest
import com.example.materna.models.requests.CancelarAgendamentoRequest
import com.example.materna.models.responses.AgendamentoResponse
import com.example.materna.models.responses.CancelarAgendamentoResponse
import com.example.materna.models.responses.HorarioAgenda
import com.example.materna.models.responses.HorariosAgendaResponse
import com.example.materna.services.materna.MaternaRetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.ZoneId

class AgendamentoScreenViewModel : ViewModel() {
    private val _tipoAtendimento = MutableLiveData<String>()
    val tipoAtendimento: LiveData<String> = _tipoAtendimento

    private val _tiposPermitidos = MutableLiveData<List<String>>(emptyList())
    val tiposPermitidos: LiveData<List<String>> = _tiposPermitidos

    private val _dataSelecionada = MutableLiveData<String?>()
    val dataSelecionada: LiveData<String?> = _dataSelecionada

    private val _horarioSelecionado = MutableLiveData<Int?>()
    val horarioSelecionado: LiveData<Int?> = _horarioSelecionado

    private val _isFormularioValido = MutableLiveData(false)
    val isFormularioValido: LiveData<Boolean> = _isFormularioValido

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _mensagemErro = MutableLiveData<String?>()
    val mensagemErro: LiveData<String?> = _mensagemErro

    private val _horarios = MutableLiveData<List<HorarioAgenda>>(emptyList())
    val horarios: LiveData<List<HorarioAgenda>> = _horarios

    private val _agendamentoExistente = MutableLiveData<HorarioAgenda?>(null)
    val agendamentoExistente: LiveData<HorarioAgenda?> = _agendamentoExistente

    private val _isBuscandoHorarios = MutableLiveData(false)
    val isBuscandoHorarios: LiveData<Boolean> = _isBuscandoHorarios

    private val _isCancelandoAgendamento = MutableLiveData(false)
    val isCancelandoAgendamento: LiveData<Boolean> = _isCancelandoAgendamento

    private val _erroCancelamento = MutableLiveData<String?>(null)
    val erroCancelamento: LiveData<String?> = _erroCancelamento

    private var consultaHorariosAtual: Call<HorariosAgendaResponse>? = null

    private var consultaHorariosConcluida = false

    private var requisicaoAgendamentoAtual: Call<AgendamentoResponse>? = null

    private var requisicaoCancelamentoAtual: Call<CancelarAgendamentoResponse>? = null

    fun configurarTipoColeta(idColeta: Int) {
        val tipos = when (idColeta) {
            1 -> listOf("domiciliar")
            2 -> listOf("presencial")
            3 -> listOf("presencial", "domiciliar")
            else -> emptyList()
        }

        if (_tiposPermitidos.value == tipos) return

        _tiposPermitidos.value = tipos

        _tipoAtendimento.value = tipos.singleOrNull().orEmpty()
        _dataSelecionada.value = null
        _horarioSelecionado.value = null

        validarFormulario()

        Log.d(
            "AGENDAMENTO_COLETA",
            "ID coleta: $idColeta | " +
                    "Permitidos: $tipos | " +
                    "Selecionado: ${_tipoAtendimento.value}"
        )
    }

    fun onTipoAtendimentoChanged(tipo: String) {
        if (tipo !in _tiposPermitidos.value.orEmpty()) return

        if (_tipoAtendimento.value == tipo) return

        _tipoAtendimento.value = tipo
        _dataSelecionada.value = null
        _horarioSelecionado.value = null

        validarFormulario()
    }

    fun onDataChanged(data: String) {
        if (_dataSelecionada.value == data) return

        _dataSelecionada.value = data
        _horarioSelecionado.value = null

        validarFormulario()
    }

    fun onHorarioChanged(idHorario: Int) {
        if (!consultaHorariosConcluida) return
        if (_isBuscandoHorarios.value == true) return
        if (_isLoading.value == true) return
        if (_agendamentoExistente.value != null) return
        if (_mensagemErro.value != null) return
        if (_dataSelecionada.value.isNullOrBlank()) return

        val horarioValido = _horarios.value.orEmpty().any {
            it.idHorario == idHorario &&
                    it.disponivel == 1 &&
                    it.isProximo == 1 &&
                    it.idAgendamento == null &&
                    it.idDoadora == null
        }

        if (!horarioValido) {
            Log.w(
                "AGENDAMENTO_VALIDACAO",
                "Tentativa de selecionar um horário inválido."
            )
            return
        }

        _horarioSelecionado.value = idHorario

        validarFormulario()
    }

    private fun validarFormulario() {
        val idHorario = _horarioSelecionado.value

        val horarioValido = _horarios.value.orEmpty().any {
            it.idHorario == idHorario &&
                    it.disponivel == 1 &&
                    it.isProximo == 1 &&
                    it.idAgendamento == null &&
                    it.idDoadora == null
        }

        _isFormularioValido.value =
            consultaHorariosConcluida &&
                    !_isBuscandoHorarios.value.orFalse() &&
                    !_isLoading.value.orFalse() &&
                    _mensagemErro.value == null &&
                    _agendamentoExistente.value == null &&
                    _tipoAtendimento.value in _tiposPermitidos.value.orEmpty() &&
                    !_dataSelecionada.value.isNullOrBlank() &&
                    horarioValido &&
                    !_isCancelandoAgendamento.value.orFalse()
    }

    private fun Boolean?.orFalse(): Boolean {
        return this == true
    }

    fun testarBuscaHorarios(
        idInstituicao: Int,
        idJornada: Int,
        idEtapa: Int,
        idDoadora: Int,
        idJornadaDoacao: Int,
        context: Context,
        preservarAgendamento: Boolean = false
    ) {
        val tag = "AGENDAMENTO_API"

        consultaHorariosAtual?.cancel()

        consultaHorariosConcluida = false

        _isBuscandoHorarios.value = true
        _mensagemErro.value = null
        _horarios.value = emptyList()
        _dataSelecionada.value = null
        _horarioSelecionado.value = null
        if (!preservarAgendamento) { _agendamentoExistente.value = null }

        validarFormulario()

        Log.d(tag, "Buscando horários da instituição: $idInstituicao")

        val requisicao = MaternaRetrofitFactory(context.applicationContext)
            .getHorarioService()
            .buscarHorariosDisponiveis(
                idInstituicao = idInstituicao,
                isProximo = true,
                idDoadora = idDoadora.toString()
            )

        consultaHorariosAtual = requisicao

        requisicao.enqueue(object : Callback<HorariosAgendaResponse> {

            override fun onResponse(
                call: Call<HorariosAgendaResponse>,
                response: Response<HorariosAgendaResponse>
            ) {
                if (call.isCanceled || consultaHorariosAtual != call) return

                consultaHorariosAtual = null
                _isBuscandoHorarios.value = false

                Log.d(tag, "HTTP: ${response.code()}")
                Log.d(tag, "URL: ${call.request().url}")

                val body = response.body()

                if (response.isSuccessful && body?.status == true) {

                    val horariosRecebidos = body.response
                        ?.horario
                        .orEmpty()

                    val agendamento = horariosRecebidos.firstOrNull {
                        it.idDoadora == idDoadora &&
                                it.idJornada == idJornada &&
                                it.idEtapa == idEtapa &&
                                it.idJornadaDoacao == idJornadaDoacao &&
                                it.idAgendamento != null &&
                                it.encerrado == 0 &&
                                it.idStatus in listOf(1, 2)
                    }

                    if (agendamento != null || !preservarAgendamento) {
                        _agendamentoExistente.value = agendamento
                    } else {
                        Log.w(
                            tag,
                            "Agendamento criado ainda não localizado na consulta."
                        )
                    }

                    Log.d(
                        "AGENDAMENTO_EXISTENTE",
                        "ID: ${agendamento?.idAgendamento} | " +
                                "Status: ${agendamento?.status} | " +
                                "Vínculo: ${agendamento?.idJornadaDoacao}"
                    )

                    val horariosDisponiveis = horariosRecebidos
                        .filter {
                            it.disponivel == 1 &&
                                    it.isProximo == 1 &&
                                    it.idDoadora == null &&
                                    it.idAgendamento == null &&
                                    it.idJornada == idJornada &&
                                    it.idEtapa == idEtapa
                        }
                        .sortedWith(
                            compareBy<HorarioAgenda> { it.data }
                                .thenBy { it.hora }
                        )

                    _horarios.value = horariosDisponiveis

                    consultaHorariosConcluida = true

                    validarFormulario()

                    Log.i(
                        tag,
                        "Horários recebidos: ${horariosRecebidos.size}"
                    )

                    Log.i(
                        tag,
                        "Horários disponíveis: ${horariosDisponiveis.size}"
                    )

                    horariosDisponiveis.forEach { horario ->
                        Log.d(
                            tag,
                            "ID: ${horario.idHorario} | " +
                                    "Etapa: ${horario.idEtapa} | " +
                                    "Data: ${horario.data} | " +
                                    "Hora: ${horario.hora}"
                        )
                    }

                } else {

                    val erro = response.errorBody()?.string()

                    if (
                        response.code() == 404 &&
                        erro.orEmpty().contains(
                            "Não foram encontrados",
                            ignoreCase = true
                        )
                    ) {
                        _horarios.value = emptyList()

                        Log.i(tag, "Nenhum horário encontrado.")

                    } else {

                        _mensagemErro.value =
                            body?.message ?: "Não foi possível buscar os horários."

                        Log.e(tag, "Erro HTTP ${response.code()}: $erro")
                    }
                }
            }

            override fun onFailure(
                call: Call<HorariosAgendaResponse>,
                t: Throwable
            ) {
                if (call.isCanceled || consultaHorariosAtual != call) return

                consultaHorariosAtual = null
                _isBuscandoHorarios.value = false

                _mensagemErro.value =
                    "Falha na comunicação com o servidor."

                Log.e(tag, "Falha ao buscar horários", t)
            }
        })
    }

    fun criarAgendamento(
        idInstituicao: Int,
        idJornada: Int,
        idEtapa: Int,
        idDoadora: Int,
        idJornadaDoacao: Int,
        context: Context
    ) {
        val tag = "AGENDAMENTO_POST"

        validarFormulario()

        if (_isFormularioValido.value != true) {
            Log.w(tag, "Formulário inválido.")
            return
        }

        if (requisicaoAgendamentoAtual != null) {
            Log.w(tag, "Já existe uma requisição em andamento.")
            return
        }

        if (
            idInstituicao <= 0 ||
            idJornada <= 0 ||
            idEtapa <= 0 ||
            idDoadora <= 0 ||
            idJornadaDoacao <= 0
        ) {
            Log.e(tag, "Identificadores inválidos.")
            return
        }

        val horario = _horarios.value.orEmpty().firstOrNull {
            it.idHorario == _horarioSelecionado.value &&
                    it.idJornada == idJornada &&
                    it.idEtapa == idEtapa &&
                    it.disponivel == 1 &&
                    it.isProximo == 1 &&
                    it.idAgendamento == null &&
                    it.idDoadora == null
        } ?: return

        val dataHorario = runCatching {
            Instant.parse(horario.data)
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toLocalDate()
                .toString()
        }.getOrNull()

        if (
            dataHorario == null ||
            dataHorario != _dataSelecionada.value
        ) {
            Log.e(tag, "Data incompatível com o horário selecionado.")
            return
        }

        val request = AgendamentoRequest(
            idCadastroHorario = horario.idHorario,
            idJornadaDoacao = idJornadaDoacao,
            idStatusAgendamento = 1
        )

        _isLoading.value = true
        _mensagemErro.value = null

        validarFormulario()

        Log.d(tag, "Iniciando criação do agendamento.")
        Log.d(tag, "Horário: ${request.idCadastroHorario}")
        Log.d(tag, "Vínculo: ${request.idJornadaDoacao}")
        Log.d(tag, "Status inicial: ${request.idStatusAgendamento}")

        val requisicao = MaternaRetrofitFactory(
            context.applicationContext
        )
            .getAgendamentoService()
            .criarAgendamento(request)

        requisicaoAgendamentoAtual = requisicao

        requisicao.enqueue(object : Callback<AgendamentoResponse> {

            override fun onResponse(
                call: Call<AgendamentoResponse>,
                response: Response<AgendamentoResponse>
            ) {
                if (
                    call.isCanceled ||
                    requisicaoAgendamentoAtual != call
                ) return

                requisicaoAgendamentoAtual = null
                consultaHorariosConcluida = false

                Log.d(tag, "HTTP: ${response.code()}")

                val body = response.body()
                val agendamentoCriado = body?.response

                if (
                    response.isSuccessful &&
                    body?.status == true &&
                    agendamentoCriado != null &&
                    agendamentoCriado.id > 0 &&
                    agendamentoCriado.idCadastroHorario == horario.idHorario &&
                    agendamentoCriado.idJornadaDoacao == idJornadaDoacao &&
                    agendamentoCriado.idStatusAgendamento == 1
                ) {
                    Log.i(tag, "Agendamento criado com sucesso!")
                    Log.d(tag, "ID: ${agendamentoCriado.id}")
                    Log.d(tag, "Mensagem: ${body.message}")

                    _agendamentoExistente.value = horario.copy(
                        idAgendamento = agendamentoCriado.id,
                        idJornadaDoacao = idJornadaDoacao,
                        idDoadora = idDoadora,
                        idStatus = 1,
                        status = "Pendente",
                        encerrado = 0
                    )

                    _horarios.value = emptyList()
                    _dataSelecionada.value = null
                    _horarioSelecionado.value = null
                    _isLoading.value = false

                    validarFormulario()

                    testarBuscaHorarios(
                        idInstituicao = idInstituicao,
                        idJornada = idJornada,
                        idEtapa = idEtapa,
                        idDoadora = idDoadora,
                        idJornadaDoacao = idJornadaDoacao,
                        context = context.applicationContext,
                        preservarAgendamento = true
                    )

                } else {
                    _isLoading.value = false

                    _mensagemErro.value =
                        "Não foi possível confirmar a criação do agendamento. " +
                                "Consulte novamente os horários."

                    Log.e(
                        tag,
                        "Resposta inválida ou erro HTTP ${response.code()}"
                    )

                    validarFormulario()
                }
            }

            override fun onFailure(
                call: Call<AgendamentoResponse>,
                t: Throwable
            ) {
                if (
                    call.isCanceled ||
                    requisicaoAgendamentoAtual != call
                ) return

                requisicaoAgendamentoAtual = null
                consultaHorariosConcluida = false

                _isLoading.value = false

                _mensagemErro.value =
                    "Não foi possível verificar o resultado da solicitação. " +
                            "Consulte novamente os horários."

                Log.e(tag, "Falha na comunicação com a API.", t)

                validarFormulario()
            }
        })
    }

    fun cancelarAgendamento(
        idInstituicao: Int,
        idJornada: Int,
        idEtapa: Int,
        idDoadora: Int,
        idJornadaDoacao: Int,
        context: Context
    ) {
        val tag = "AGENDAMENTO_CANCELAMENTO"

        if (
            !consultaHorariosConcluida ||
            _isCancelandoAgendamento.value == true ||
            _isLoading.value == true ||
            _isBuscandoHorarios.value == true ||
            requisicaoCancelamentoAtual != null
        ) return

        val agendamento = _agendamentoExistente.value ?: return
        val idAgendamento = agendamento.idAgendamento ?: return

        if (
            idAgendamento <= 0 ||
            agendamento.idStatus !in listOf(1, 2) ||
            agendamento.encerrado != 0 ||
            agendamento.idDoadora != idDoadora ||
            agendamento.idJornada != idJornada ||
            agendamento.idEtapa != idEtapa ||
            agendamento.idJornadaDoacao != idJornadaDoacao
        ) {
            Log.w(tag, "Agendamento inválido para cancelamento.")

            _erroCancelamento.value =
                "Não foi possível identificar o agendamento."

            return
        }

        _isCancelandoAgendamento.value = true
        _erroCancelamento.value = null

        consultaHorariosConcluida = false

        validarFormulario()

        val request = CancelarAgendamentoRequest(
            idDoadora = idDoadora,
            idStatusAgendamento = 4
        )

        Log.d(tag, "Iniciando cancelamento.")
        Log.d(tag, "Agendamento: $idAgendamento")
        Log.d(tag, "Vínculo: $idJornadaDoacao")

        val requisicao = MaternaRetrofitFactory(
            context.applicationContext
        )
            .getAgendamentoService()
            .cancelarAgendamento(
                idAgendamento = idAgendamento,
                request = request
            )

        requisicaoCancelamentoAtual = requisicao

        requisicao.enqueue(
            object : Callback<CancelarAgendamentoResponse> {

                override fun onResponse(
                    call: Call<CancelarAgendamentoResponse>,
                    response: Response<CancelarAgendamentoResponse>
                ) {
                    if (
                        call.isCanceled ||
                        requisicaoCancelamentoAtual != call
                    ) return

                    requisicaoCancelamentoAtual = null

                    Log.d(tag, "HTTP: ${response.code()}")

                    val body = response.body()
                    val cancelamento = body?.response

                    if (
                        response.isSuccessful &&
                        body?.status == true &&
                        cancelamento != null &&
                        cancelamento.id == idAgendamento &&
                        cancelamento.idDoadora == idDoadora &&
                        cancelamento.idStatusAgendamento == 4
                    ) {
                        Log.i(
                            tag,
                            "Agendamento cancelado com sucesso!"
                        )

                        _agendamentoExistente.value = null
                        _horarios.value = emptyList()
                        _dataSelecionada.value = null
                        _horarioSelecionado.value = null

                        _isCancelandoAgendamento.value = false

                        validarFormulario()

                        testarBuscaHorarios(
                            idInstituicao = idInstituicao,
                            idJornada = idJornada,
                            idEtapa = idEtapa,
                            idDoadora = idDoadora,
                            idJornadaDoacao = idJornadaDoacao,
                            context = context.applicationContext
                        )

                    } else {
                        _isCancelandoAgendamento.value = false

                        _erroCancelamento.value =
                            "Não foi possível confirmar o cancelamento. " +
                                    "Atualize os dados antes de tentar novamente."

                        Log.e(
                            tag,
                            "Resposta inválida ou HTTP ${response.code()}"
                        )

                        validarFormulario()
                    }
                }

                override fun onFailure(
                    call: Call<CancelarAgendamentoResponse>,
                    t: Throwable
                ) {
                    if (
                        call.isCanceled ||
                        requisicaoCancelamentoAtual != call
                    ) return

                    requisicaoCancelamentoAtual = null

                    _isCancelandoAgendamento.value = false

                    _erroCancelamento.value =
                        "Não foi possível verificar o cancelamento. " +
                                "Atualize os dados antes de tentar novamente."

                    Log.e(
                        tag,
                        "Falha na comunicação com a API.",
                        t
                    )

                    validarFormulario()
                }
            }
        )
    }

    fun limparErroCancelamento() {
        _erroCancelamento.value = null
    }

    fun limparMensagemErro() {
        _mensagemErro.value = null
    }

    override fun onCleared() {
        consultaHorariosAtual?.cancel()
        consultaHorariosAtual = null

        requisicaoAgendamentoAtual?.cancel()
        requisicaoAgendamentoAtual = null

        requisicaoCancelamentoAtual?.cancel()
        requisicaoCancelamentoAtual = null

        super.onCleared()
    }
}