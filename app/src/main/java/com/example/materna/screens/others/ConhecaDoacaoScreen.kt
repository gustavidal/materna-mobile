package com.example.materna.screens.others

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun ConhecaDoacaoScreen(
    onConcluir: () -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarAvisoSaida by remember {
        mutableStateOf(false)
    }

    BackHandler {
        mostrarAvisoSaida = true
    }

    if (mostrarAvisoSaida) {
        AlertDialog(
            onDismissRequest = {
                mostrarAvisoSaida = false
            },
            title = {
                Text("Deseja sair da jornada?")
            },
            text = {
                Text(
                    "Você ainda não concluiu a primeira etapa. " +
                            "Se sair agora, sua jornada ainda não estará " +
                            "vinculada à instituição selecionada."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarAvisoSaida = false
                        onVoltar()
                    }
                ) {
                    Text("Sair mesmo assim")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarAvisoSaida = false
                    }
                ) {
                    Text("Continuar leitura")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Conheça a doação",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.main_text_color)
        )

        Text(
            text = "Um pequeno gesto pode fazer uma grande diferença.",
            fontSize = 16.sp,
            color = colorResource(R.color.main_text_color)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.white)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Por que doar leite materno?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "O leite materno é essencial para a saúde e o desenvolvimento dos bebês. " +
                            "Quando uma mãe não consegue amamentar seu filho, o leite humano doado " +
                            "pode contribuir para a alimentação de recém-nascidos que precisam " +
                            "desse cuidado, especialmente os prematuros."
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.white)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Como funciona a doação?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "O processo envolve orientações, avaliação das condições " +
                            "de saúde da doadora e acompanhamento pelo banco de leite humano. " +
                            "O leite doado passa por procedimentos de controle de qualidade " +
                            "antes de ser destinado aos bebês."
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.white)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Quem pode doar?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Mulheres que amamentam, estão saudáveis e produzem leite " +
                            "além da necessidade de seus bebês podem ser candidatas à doação. " +
                            "A equipe do banco de leite avaliará os critérios necessários."
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onConcluir,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.main_pink)
            )
        ) {
            Text(
                text = "Concluir leitura",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
