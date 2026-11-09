package com.example.materna.screens.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.PerfilIndicador
import com.example.materna.components.PerfilOpcao

@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    perfilScreenViewModel: PerfilScreenViewModel = viewModel()
) {
    val doadora      by perfilScreenViewModel.doadora.observeAsState(null)
    val carregando   by perfilScreenViewModel.carregando.observeAsState(false)
    val mensagemErro by perfilScreenViewModel.mensagemErro.observeAsState(null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = colorResource(R.color.background_gray),
                        shape = CircleShape
                    )
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Voltar",
                    tint = colorResource(R.color.main_text_color)
                )
            }

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = "Perfil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_text_color)
                )

                Text(
                    text = "Suas informações",
                    fontSize = 14.sp,
                    color = colorResource(R.color.secundary_gray)
                )
            }
        }

        Spacer(Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .size(112.dp)
                .background(
                    color = colorResource(R.color.main_pink).copy(alpha = 0.12f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar da doadora",
                modifier = Modifier.size(58.dp),
                tint = colorResource(R.color.main_pink)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (carregando) {
            CircularProgressIndicator(
                color = colorResource(R.color.main_pink),
                modifier = Modifier.size(28.dp)
            )
        } else {
            Text(
                text = doadora?.nome ?: "Nome não disponível",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.main_text_color)
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Doadora",
            fontSize = 14.sp,
            color = colorResource(R.color.secundary_gray)
        )

        if (mensagemErro != null) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = mensagemErro.orEmpty(),
                fontSize = 13.sp,
                color = colorResource(R.color.red)
            )
        }

        Spacer(Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PerfilIndicador(
                valor = "—",
                descricao = "Etapas da jornada",
                cor = colorResource(R.color.main_pink),
                modifier = Modifier.weight(1f)
            )

            PerfilIndicador(
                valor = "—",
                descricao = "Doações realizadas",
                cor = colorResource(R.color.green),
                modifier = Modifier.weight(1f)
            )

            PerfilIndicador(
                valor = "—",
                descricao = "Agendamentos",
                cor = colorResource(R.color.main_text_color),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(30.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(R.color.white),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(vertical = 8.dp)
        ) {
            PerfilOpcao(
                titulo = "Meus agendamentos",
                simbolo = "▦",
                onClick = {
                    // Aguardando rota de agendamentos.
                }
            )

            PerfilOpcao(
                titulo = "Editar informações",
                simbolo = "✎",
                onClick = {
                    navController.navigate("editar_perfil")
                }
            )

            PerfilOpcao(
                titulo = "Sobre o Materna",
                simbolo = "ⓘ",
                onClick = {
                    // Aguardando tela Sobre.
                }
            )
        }

        Spacer(Modifier.height(28.dp))

        OutlinedButton(
            onClick = {
                // Logout será implementado com a autenticação.
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = CircleShape,
            border = BorderStroke(
                width = 1.5.dp,
                color = colorResource(R.color.main_pink)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colorResource(R.color.main_pink)
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.size(10.dp))

            Text(
                text = "Sair da conta",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}