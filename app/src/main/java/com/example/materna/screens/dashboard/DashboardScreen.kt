package com.example.materna.screens.dashboard

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.CampanhaCard
import com.example.materna.components.DashboardActionCard
import com.example.materna.components.DashboardHighlightCard

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    dashboardScreenViewModel: DashboardScreenViewModel,
) {
    val nomeDoadora by dashboardScreenViewModel.nomeDoadora.observeAsState("")
    val bancoSelecionado by dashboardScreenViewModel.bancoSelecionado.observeAsState(false)

    val campanhas by dashboardScreenViewModel.campanhas.observeAsState(emptyList())
    val carregandoCampanhas by dashboardScreenViewModel.carregandoCampanhas.observeAsState(false)
    val mensagemCampanhas by dashboardScreenViewModel.mensagemCampanhas.observeAsState(null)

    LaunchedEffect(dashboardScreenViewModel) {
        dashboardScreenViewModel.buscarCampanhas()
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colorResource(R.color.background_cream))
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 32.dp,
                    bottom = 32.dp,
                ),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Olá,",
                    fontSize = 16.sp,
                    color = colorResource(R.color.secundary_gray),
                )

                Text(
                    text = nomeDoadora.trim().substringBefore(" ").ifBlank { "Doadora" } + "!",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_text_color),
                )
            }

            IconButton(
                onClick = {
                    //                    TODO: Navegar para notificações
                },
                modifier =
                    Modifier.size(48.dp)
                        .background(
                            colorResource(R.color.pink_beige),
                            CircleShape,
                        ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_notification),
                    contentDescription = "Notificações",
                    tint = colorResource(R.color.main_text_color),
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        if (!bancoSelecionado) {
            DashboardHighlightCard(
                onClick = {
                    //                    TODO: Navegar para seleção do banco
                },
            )
        }

        Text(
            text = "Como podemos ajudar?",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.main_text_color),
        )

        DashboardActionCard(
            titulo = "Quero doar leite",
            descricao = "Comece ou continue sua jornada",
            icon = painterResource(R.drawable.ic_milk_bottle),
            iconColor = colorResource(R.color.white),
            destaque = true,
            onClick = {
                navController.navigate("jornada")
            },
        )

        DashboardActionCard(
            titulo = "Encontrar serviço",
            descricao = "Bancos de leite próximos",
            icon = painterResource(R.drawable.ic_pin_location),
            iconColor = colorResource(R.color.main_pink),
            onClick = {
                navController.navigate("servicos")
            },
        )

        DashboardActionCard(
            titulo = "Aprender",
            descricao = "Informações e cuidados",
            icon = painterResource(R.drawable.ic_book),
            iconColor = colorResource(R.color.main_pink),
            onClick = {
                //                TODO: Navegar para conteúdos educativos
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Campanhas próximas",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.main_text_color),
            )

            Text(
                text = "Ver todas",
                color = colorResource(R.color.main_pink),
                modifier =
                    Modifier.clickable {
                        navController.navigate("campanhas")
                    },
            )
        }

        when {
            carregandoCampanhas -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(190.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.main_pink),
                    )
                }
            }

            mensagemCampanhas != null -> {
                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(190.dp)
                            .background(
                                colorResource(R.color.pink_beige),
                                RoundedCornerShape(18.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = mensagemCampanhas.orEmpty(),
                        color = colorResource(R.color.secundary_gray),
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            campanhas.isEmpty() -> {
                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(190.dp)
                            .background(
                                colorResource(R.color.pink_beige),
                                RoundedCornerShape(18.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Nenhuma campanha disponível no momento.",
                        color = colorResource(R.color.secundary_gray),
                    )
                }
            }

            else -> {
                campanhas.firstOrNull()?.let { campanha ->
                    CampanhaCard(
                        campanha = campanha,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate("campanha/${campanha.id}")
                        },
                    )
                }
            }
        }
    }
}
