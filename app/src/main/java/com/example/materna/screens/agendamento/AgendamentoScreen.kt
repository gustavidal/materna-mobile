package com.example.materna.screens.agendamento

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
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
import com.example.materna.components.TipoAtendimentoCard
import com.example.materna.components.DataAgendamentoCard
import com.example.materna.components.HorarioAgendamentoCard

@Composable
fun AgendamentoScreen(
    nomeInstituicao: String,
    idInstituicao: Int,
    iconeInstituicao: Painter,
    iconeHorario: ImageVector,
    onVoltar: () -> Unit,
    onConfirmar: () -> Unit,
    agendamentoScreenViewModel: AgendamentoScreenViewModel = viewModel()
) {
    val tipoAtendimento by agendamentoScreenViewModel
        .tipoAtendimento.observeAsState("")

    val dataSelecionada by agendamentoScreenViewModel
        .dataSelecionada.observeAsState("")

    val horarioSelecionado by agendamentoScreenViewModel
        .horarioSelecionado.observeAsState()

    val isFormularioValido by agendamentoScreenViewModel
        .isFormularioValido.observeAsState(false)

    val isLoading by agendamentoScreenViewModel
        .isLoading.observeAsState(false)

    val pink = colorResource(R.color.main_pink)
    val texto = colorResource(R.color.main_text_color)
    val cinza = colorResource(R.color.secundary_gray)
    val white = colorResource(R.color.white)

    // Dados temporários para testar a interface.
    val datas = listOf(
        Triple("Ter", "24", "Nov"),
        Triple("Qua", "25", "Nov"),
        Triple("Qui", "26", "Nov"),
        Triple("Sex", "27", "Nov"),
        Triple("Sáb", "28", "Nov")
    )

    val horarios = listOf(
        "09:00",
        "10:00",
        "11:00",
        "14:00",
        "15:00",
        "16:00"
    )

    val context = LocalContext.current

    LaunchedEffect(idInstituicao) {
        agendamentoScreenViewModel.testarBuscaHorarios(
            idInstituicao = idInstituicao,
            context = context.applicationContext
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream))
            .padding(horizontal = 20.dp)
    ) {

        // Cabeçalho
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onVoltar,
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        colorResource(R.color.background_gray),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = texto
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Agendamento",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = texto
                )

                Text(
                    text = nomeInstituicao,
                    fontSize = 13.sp,
                    color = cinza
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            // Tipo de atendimento
            Text(
                text = "TIPO DE ATENDIMENTO",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = texto
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TipoAtendimentoCard(
                    titulo = "Presencial",
                    descricao = "Na instituição",
                    icone = iconeInstituicao,
                    selecionado = tipoAtendimento == "presencial",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        agendamentoScreenViewModel.onTipoAtendimentoChanged("presencial")
                    }
                )

                TipoAtendimentoCard(
                    titulo = "A domicílio",
                    descricao = "Em sua residência",
                    icone = painterResource(R.drawable.ic_house),
                    selecionado = tipoAtendimento == "domiciliar",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        agendamentoScreenViewModel.onTipoAtendimentoChanged("domiciliar")
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Seleção de data
            Text(
                text = "Selecione uma data",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = texto
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(datas) { data ->

                    val dataCompleta = "2026-11-${data.second}"

                    DataAgendamentoCard(
                        diaSemana = data.first,
                        dia = data.second,
                        mes = data.third,
                        selecionado = dataSelecionada == dataCompleta,
                        onClick = {
                            agendamentoScreenViewModel
                                .onDataChanged(dataCompleta)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Seleção de horário
            Text(
                text = "Selecione um horário",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = texto
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (dataSelecionada.isNullOrBlank()) {

                Text(
                    text = "Selecione uma data para visualizar os horários.",
                    fontSize = 13.sp,
                    color = cinza
                )

            } else {

                horarios.withIndex()
                    .toList()
                    .chunked(3)
                    .forEach { linha ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {
                            linha.forEach { item ->

                                val idHorario = item.index + 1

                                val disponivel =
                                    item.index != 0 &&
                                            item.index != 4

                                HorarioAgendamentoCard(
                                    horario = item.value,
                                    disponivel = disponivel,
                                    selecionado =
                                        horarioSelecionado == idHorario,
                                    icone = iconeHorario,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        agendamentoScreenViewModel
                                            .onHorarioChanged(idHorario)
                                    }
                                )
                            }

                            repeat(3 - linha.size) {
                                Spacer(
                                    modifier = Modifier.weight(1f)
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendaHorario(
                        cor = white,
                        descricao = "Livre"
                    )

                    LegendaHorario(
                        cor = pink,
                        descricao = "Selecionado"
                    )

                    LegendaHorario(
                        cor = colorResource(R.color.background_gray),
                        descricao = "Indisponível"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Botão de confirmação
        Button(
            onClick = onConfirmar,
            enabled = isFormularioValido && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(54.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = pink,
                contentColor = white,
                disabledContainerColor = pink.copy(alpha = 0.4f),
                disabledContentColor = white
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = white,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Confirmar",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun LegendaHorario(
    cor: Color,
    descricao: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(cor, CircleShape)
                .border(
                    width = 1.dp,
                    color = colorResource(R.color.border),
                    shape = CircleShape
                )
        )

        Text(
            text = descricao,
            fontSize = 10.sp,
            color = colorResource(R.color.secundary_gray)
        )
    }
}
