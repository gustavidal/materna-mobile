
package com.example.materna.screens.jornada

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.example.materna.data.local.JornadaLocalProgress
import com.example.materna.data.local.JornadaLocalProgressDao
import kotlinx.coroutines.launch

private data class Orientacao(
    val id: String,
    val titulo: String,
    val descricao: String,
    val conteudo: String
)

private val orientacoes = listOf(
    Orientacao(
        "antes_coleta",
        "Antes da coleta",
        "Preparando o ambiente e a si mesma",
        "Escolha um local limpo e tranquilo. Separe os materiais " +
            "necessários e organize tudo antes de iniciar a coleta."
    ),
    Orientacao(
        "higiene",
        "Higiene",
        "Cuidados essenciais de limpeza",
        "Lave bem as mãos e os antebraços com água e sabão. " +
            "Utilize recipientes adequados e higienizados."
    ),
    Orientacao(
        "cuidados",
        "Cuidados",
        "Cuidados durante a ordenha",
        "Realize a retirada do leite com cuidado, evitando " +
            "contato das mãos com a parte interna do recipiente."
    ),
    Orientacao(
        "armazenamento",
        "Armazenamento",
        "Conservação correta do leite",
        "Armazene o leite em recipiente apropriado, " +
            "identificado e mantido congelado conforme " +
            "as orientações do banco de leite."
    ),
    Orientacao(
        "transporte",
        "Transporte",
        "Levando até o banco de leite",
        "Mantenha o leite congelado durante o transporte, " +
            "utilizando recipiente térmico adequado."
    ),
    Orientacao(
        "entrega",
        "Entrega",
        "Finalizando a doação",
        "Combine a entrega com o banco de leite e siga " +
            "as orientações da equipe responsável."
    )
)

@Composable
fun OrientacoesScreen(
    idDoadora: Int?,
    idJornada: Int?,
    progressoDao: JornadaLocalProgressDao,
    onVoltar: () -> Unit,
    onConcluir: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val podeSalvar = idDoadora != null &&
        idDoadora > 0 &&
        idJornada != null &&
        idJornada > 0

    val progresso by remember(idDoadora, idJornada, podeSalvar) {
        if (podeSalvar) {
            progressoDao.observarProgresso(
                idDoadora = requireNotNull(idDoadora),
                idJornada = requireNotNull(idJornada)
            )
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())

    val topicosVisualizados = progresso
        .filter { it.concluida && it.idEtapa.startsWith("orientacoes_") }
        .map { it.idEtapa.removePrefix("orientacoes_") }
        .toSet()

    val todosVisualizados = orientacoes.all {
        it.id in topicosVisualizados
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "‹  Voltar",
            modifier = Modifier.clickable { onVoltar() },
            color = colorResource(R.color.main_text_color)
        )

        Text(
            text = "Orientações",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.main_text_color)
        )

        Text(
            text = "Etapa 2",
            fontSize = 13.sp,
            color = colorResource(R.color.secundary_gray)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Text(
            text = "Toque em cada tema para ler as orientações " +
                "completas sobre a coleta e a doação do leite humano.",
            fontSize = 14.sp,
            color = colorResource(R.color.main_text_color)
        )

        orientacoes.forEach { orientacao ->
            OrientacaoCard(
                orientacao = orientacao,
                visualizado = orientacao.id in topicosVisualizados,
                onVisualizar = {
                    if (podeSalvar) {
                        scope.launch {
                            progressoDao.salvarConclusao(
                                JornadaLocalProgress(
                                    idDoadora = requireNotNull(idDoadora),
                                    idJornada = requireNotNull(idJornada),
                                    idEtapa = "orientacoes_${orientacao.id}"
                                )
                            )
                        }
                    }
                }
            )
        }

        Button(
            onClick = onConcluir,
            enabled = todosVisualizados,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.main_pink)
            )
        ) {
            Text("Concluir orientações")
        }
    }
}

@Composable
private fun OrientacaoCard(
    orientacao: Orientacao,
    visualizado: Boolean,
    onVisualizar: () -> Unit
) {
    var expandido by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expandido = !expandido

                if (expandido) {
                    onVisualizar()
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.white)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = orientacao.titulo,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.main_text_color)
                    )

                    Text(
                        text = orientacao.descricao,
                        fontSize = 12.sp,
                        color = colorResource(R.color.secundary_gray)
                    )
                }

                Text(
                    text = if (visualizado) "✓" else "›",
                    color = colorResource(
                        if (visualizado) R.color.success_green
                        else R.color.main_text_color
                    )
                )
            }

            AnimatedVisibility(
                visible = expandido,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = orientacao.conteudo,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = colorResource(R.color.main_text_color)
                )
            }
        }
    }
}
