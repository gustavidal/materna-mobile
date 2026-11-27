package com.example.materna.screens.agendamento

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.materna.R
import com.example.materna.components.AgendamentoStatusCard
import com.example.materna.components.DataAgendamentoCard
import com.example.materna.components.HorarioAgendamentoCard
import com.example.materna.components.TipoAtendimentoCard
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AgendamentoScreen(
    nomeInstituicao: String,
    idInstituicao: Int,
    idJornada: Int,
    idEtapa: Int,
    idColeta: Int,
    idDoadora: Int,
    idJornadaDoacao: Int,
    iconeInstituicao: Painter,
    iconeHorario: ImageVector,
    onVoltar: () -> Unit,
    agendamentoScreenViewModel: AgendamentoScreenViewModel = viewModel(),
) {
    val tipoAtendimento by agendamentoScreenViewModel.tipoAtendimento.observeAsState("")

    val tiposPermitidos by agendamentoScreenViewModel.tiposPermitidos.observeAsState(emptyList())

    val dataSelecionada by agendamentoScreenViewModel.dataSelecionada.observeAsState("")

    val horarioSelecionado by agendamentoScreenViewModel.horarioSelecionado.observeAsState()

    val isFormularioValido by agendamentoScreenViewModel.isFormularioValido.observeAsState(false)

    val isLoading by agendamentoScreenViewModel.isLoading.observeAsState(false)

    val horariosDisponiveis by agendamentoScreenViewModel.horarios.observeAsState(emptyList())

    val isBuscandoHorarios by agendamentoScreenViewModel.isBuscandoHorarios.observeAsState(false)

    val mensagemErro by agendamentoScreenViewModel.mensagemErro.observeAsState()

    val agendamentoExistente by agendamentoScreenViewModel.agendamentoExistente.observeAsState()

    val isCancelandoAgendamento by agendamentoScreenViewModel.isCancelandoAgendamento.observeAsState(false)

    val erroCancelamento by agendamentoScreenViewModel.erroCancelamento.observeAsState()

    var exibirDialogCancelamento by remember {
        mutableStateOf(false)
    }

    val pink = colorResource(R.color.main_pink)
    val texto = colorResource(R.color.main_text_color)
    val cinza = colorResource(R.color.secundary_gray)
    val white = colorResource(R.color.white)

    val locale = Locale("pt", "BR")
    val zona = ZoneId.of("America/Sao_Paulo")

    val horariosPorData = horariosDisponiveis.groupBy {
        Instant.parse(it.data).atZone(zona).toLocalDate()
    }

    val datas = horariosPorData.keys.sorted()

    val horarios =
        horariosPorData.entries
            .firstOrNull {
                it.key.toString() == dataSelecionada
            }
            ?.value
            .orEmpty()
            .sortedBy { it.hora }

    val context = LocalContext.current

    LaunchedEffect(idColeta) {
        agendamentoScreenViewModel.configurarTipoColeta(idColeta)
    }

    LaunchedEffect(
        idInstituicao,
        idJornada,
        idEtapa,
        idDoadora,
        idJornadaDoacao,
    ) {
        agendamentoScreenViewModel.testarBuscaHorarios(
            idInstituicao = idInstituicao,
            idJornada = idJornada,
            idEtapa = idEtapa,
            idDoadora = idDoadora,
            idJornadaDoacao = idJornadaDoacao,
            context = context.applicationContext,
        )
    }

    if (exibirDialogCancelamento) {
        AlertDialog(
            onDismissRequest = {
                exibirDialogCancelamento = false
            },
            title = {
                Text(
                    text = "Cancelar agendamento?",
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text =
                        "Tem certeza de que deseja cancelar " +
                            "seu agendamento? Você poderá solicitar " +
                            "um novo horário disponível posteriormente.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        exibirDialogCancelamento = false

                        agendamentoScreenViewModel.cancelarAgendamento(
                            idInstituicao = idInstituicao,
                            idJornada = idJornada,
                            idEtapa = idEtapa,
                            idDoadora = idDoadora,
                            idJornadaDoacao = idJornadaDoacao,
                            context = context.applicationContext,
                        )
                    },
                ) {
                    Text(
                        text = "Sim, cancelar",
                        color = pink,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        exibirDialogCancelamento = false
                    },
                ) {
                    Text(
                        text = "Manter agendamento",
                        color = pink,
                    )
                }
            },
            containerColor = white,
            titleContentColor = colorResource(R.color.main_text_color),
            textContentColor = colorResource(R.color.main_text_color),
            shape = RoundedCornerShape(24.dp),
        )
    }

    Column(
        modifier =
            Modifier.fillMaxSize()
                .background(colorResource(R.color.background_cream))
                .padding(horizontal = 20.dp),
    ) {

        // Cabeçalho
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onVoltar,
                modifier =
                    Modifier.size(42.dp)
                        .background(
                            colorResource(R.color.background_gray),
                            CircleShape,
                        ),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = texto,
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Agendamento",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = texto,
                )

                Text(
                    text = nomeInstituicao,
                    fontSize = 13.sp,
                    color = cinza,
                )
            }
        }

        val agendamento = agendamentoExistente

        if (agendamento != null) {

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                AgendamentoStatusCard(
                    agendamento = agendamento,
                    iconeStatus = iconeHorario,
                    onCancelar = {
                        exibirDialogCancelamento = true
                    },
                    cancelamentoHabilitado =
                        !isCancelandoAgendamento &&
                            !isBuscandoHorarios &&
                            !isLoading &&
                            mensagemErro == null &&
                            erroCancelamento == null,
                    isCancelando = isCancelandoAgendamento,
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = onVoltar,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(54.dp),
                shape = RoundedCornerShape(30.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = pink,
                        contentColor = white,
                    ),
            ) {
                Text(
                    text = "Voltar para jornada",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        } else {

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            ) {

                // Tipo de atendimento
                Text(
                    text = "TIPO DE ATENDIMENTO",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = texto,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if ("presencial" in tiposPermitidos) {
                        TipoAtendimentoCard(
                            titulo = "Presencial",
                            descricao = "Na instituição",
                            icone = iconeInstituicao,
                            selecionado = tipoAtendimento == "presencial",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                agendamentoScreenViewModel.onTipoAtendimentoChanged("presencial")
                            },
                        )
                    }

                    if ("domiciliar" in tiposPermitidos) {
                        TipoAtendimentoCard(
                            titulo = "A domicílio",
                            descricao = "Em sua residência",
                            icone = painterResource(R.drawable.ic_house),
                            selecionado = tipoAtendimento == "domiciliar",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                agendamentoScreenViewModel.onTipoAtendimentoChanged("domiciliar")
                            },
                        )
                    }

                    if (tiposPermitidos.isEmpty()) {
                        Text(
                            text = "Nenhuma modalidade de atendimento disponível.",
                            fontSize = 13.sp,
                            color = cinza,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Seleção de data
                Text(
                    text = "Selecione uma data",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = texto,
                )

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    isBuscandoHorarios -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(90.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                color = pink,
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 3.dp,
                            )
                        }
                    }

                    mensagemErro != null -> {
                        Text(
                            text = mensagemErro.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                        )
                    }

                    datas.isEmpty() -> {
                        Text(
                            text = "Nenhuma data disponível para esta etapa.",
                            fontSize = 13.sp,
                            color = cinza,
                        )
                    }

                    else -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(
                                items = datas,
                                key = { it.toString() },
                            ) { data ->
                                val dataCompleta = data.toString()

                                DataAgendamentoCard(
                                    diaSemana =
                                        data
                                            .format(
                                                DateTimeFormatter.ofPattern("EEE", locale),
                                            )
                                            .removeSuffix(".")
                                            .replaceFirstChar { it.uppercase() },
                                    dia =
                                        data.format(
                                            DateTimeFormatter.ofPattern("dd"),
                                        ),
                                    mes =
                                        data
                                            .format(
                                                DateTimeFormatter.ofPattern("MMM", locale),
                                            )
                                            .removeSuffix(".")
                                            .replaceFirstChar { it.uppercase() },
                                    selecionado = dataSelecionada == dataCompleta,
                                    onClick = {
                                        agendamentoScreenViewModel.onDataChanged(dataCompleta)
                                    },
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Seleção de horário
                Text(
                    text = "Selecione um horário",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = texto,
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (dataSelecionada.isNullOrBlank()) {

                    Text(
                        text = "Selecione uma data para visualizar os horários.",
                        fontSize = 13.sp,
                        color = cinza,
                    )
                } else {

                    horarios.chunked(3).forEach { linha ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            linha.forEach { item ->
                                HorarioAgendamentoCard(
                                    horario = item.hora.take(5),
                                    disponivel = item.disponivel == 1,
                                    selecionado = horarioSelecionado == item.idHorario,
                                    icone = iconeHorario,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        if (item.disponivel == 1) {
                                            agendamentoScreenViewModel.onHorarioChanged(
                                                item.idHorario,
                                            )
                                        }
                                    },
                                )
                            }

                            repeat(3 - linha.size) {
                                Spacer(
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legenda
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LegendaHorario(
                            cor = white,
                            descricao = "Livre",
                        )

                        LegendaHorario(
                            cor = pink,
                            descricao = "Selecionado",
                        )

                        LegendaHorario(
                            cor = colorResource(R.color.background_gray),
                            descricao = "Indisponível",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Botão de confirmação
            Button(
                onClick = {
                    agendamentoScreenViewModel.criarAgendamento(
                        idInstituicao = idInstituicao,
                        idJornada = idJornada,
                        idEtapa = idEtapa,
                        idDoadora = idDoadora,
                        idJornadaDoacao = idJornadaDoacao,
                        context = context.applicationContext,
                    )
                },
                enabled =
                    isFormularioValido &&
                        !isLoading &&
                        !isBuscandoHorarios &&
                        mensagemErro == null &&
                        agendamentoExistente == null,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(54.dp),
                shape = RoundedCornerShape(30.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = pink,
                        contentColor = white,
                        disabledContainerColor = pink.copy(alpha = 0.4f),
                        disabledContentColor = white,
                    ),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = white,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = "Confirmar",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendaHorario(
    cor: Color,
    descricao: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier =
                Modifier.size(12.dp)
                    .background(cor, CircleShape)
                    .border(
                        width = 1.dp,
                        color = colorResource(R.color.border),
                        shape = CircleShape,
                    ),
        )

        Text(
            text = descricao,
            fontSize = 10.sp,
            color = colorResource(R.color.secundary_gray),
        )
    }
}
