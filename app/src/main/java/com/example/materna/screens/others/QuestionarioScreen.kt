package com.example.materna.screens.others

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.materna.data.local.JornadaLocalProgress
import com.example.materna.data.local.JornadaLocalProgressDao
import kotlinx.coroutines.launch
import com.example.materna.R

private data class PerguntaQuestionario(
    val texto: String,
    val opcoes: List<String>
)

private val perguntasQuestionario = listOf(
    PerguntaQuestionario(
        "Você está amamentando atualmente?",
        listOf("Sim", "Não")
    ),
    PerguntaQuestionario(
        "Você acredita produzir leite além da necessidade do seu bebê?",
        listOf("Sim", "Não", "Não tenho certeza")
    ),
    PerguntaQuestionario(
        "Você está em boas condições de saúde atualmente?",
        listOf("Sim", "Não", "Não tenho certeza")
    ),
    PerguntaQuestionario(
        "Você utiliza algum medicamento regularmente?",
        listOf("Sim", "Não")
    ),
    PerguntaQuestionario(
        "Você fuma ou utiliza produtos com nicotina?",
        listOf("Sim", "Não")
    ),
    PerguntaQuestionario(
        "Você consome bebidas alcoólicas?",
        listOf("Sim", "Não")
    ),
    PerguntaQuestionario(
        "Você já recebeu orientações sobre a coleta e o armazenamento de leite humano?",
        listOf("Sim", "Não")
    )
)

@Composable
fun QuestionarioScreen(
    idDoadora: Int?,
    idJornada: Int?,
    progressoDao: JornadaLocalProgressDao,
    onVoltar: () -> Unit,
    onConcluir: () -> Unit
) {
    var indiceAtual by remember { mutableStateOf(0) }

    val respostas = remember {
        mutableStateMapOf<Int, String>()
    }

    val pergunta = perguntasQuestionario[indiceAtual]
    val respostaAtual = respostas[indiceAtual]

    val scope = rememberCoroutineScope()

    val podeSalvar = idDoadora != null &&
            idDoadora > 0 &&
            idJornada != null &&
            idJornada > 0

    var salvando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .background(colorResource(R.color.background_cream)),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Questionário de Pré-Triagem",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Pergunta ${indiceAtual + 1} de ${perguntasQuestionario.size}"
        )

        LinearProgressIndicator(
            progress = {
                (indiceAtual + 1).toFloat() / perguntasQuestionario.size
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = pergunta.texto,
            style = MaterialTheme.typography.titleLarge
        )

        pergunta.opcoes.forEach { opcao ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = respostaAtual == opcao,
                    onClick = {
                        respostas[indiceAtual] = opcao
                    }
                )

                TextButton(
                    onClick = {
                        respostas[indiceAtual] = opcao
                    }
                ) {
                    Text(opcao)
                }
            }
        }

        Text(
            text = "Este questionário é informativo e não substitui a avaliação do Banco de Leite Humano.",
            style = MaterialTheme.typography.bodySmall
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    if (indiceAtual > 0) {
                        indiceAtual--
                    } else {
                        onVoltar()
                    }
                }
            ) {
                Text("Voltar")
            }

            Button(
                enabled = respostaAtual != null && !salvando && (indiceAtual < perguntasQuestionario.lastIndex || podeSalvar),
                onClick = {
                    if (indiceAtual < perguntasQuestionario.lastIndex) {
                        indiceAtual++
                    } else if (podeSalvar && !salvando &&
                        respostas.size == perguntasQuestionario.size
                    ) {
                        salvando = true

                        scope.launch {
                            try {
                                progressoDao.salvarConclusao(
                                    JornadaLocalProgress(
                                        idDoadora = requireNotNull(idDoadora),
                                        idJornada = requireNotNull(idJornada),
                                        idEtapa = "questionario"
                                    )
                                )

                                onConcluir()
                            } finally {
                                salvando = false
                            }
                        }
                    }
                }
            ) {
                Text(
                    if (indiceAtual == perguntasQuestionario.lastIndex)
                        "Finalizar"
                    else
                        "Continuar"
                )
            }
        }
    }
}
