package com.example.materna.screens.campanhas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.CampanhaCard
import com.example.materna.screens.dashboard.DashboardScreenViewModel
import java.time.Instant
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampanhasScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    dashboardScreenViewModel: DashboardScreenViewModel,
) {
    val campanhas by dashboardScreenViewModel.campanhas.observeAsState(emptyList())
    val carregando by dashboardScreenViewModel.carregandoCampanhas.observeAsState(false)
    val mensagem by dashboardScreenViewModel.mensagemCampanhas.observeAsState(null)

    val agora = Instant.now()

    val campanhasVigentes = campanhas.filter { campanha ->
        try {
            campanha.isAtivo == 1 &&
                !agora.isBefore(Instant.parse(campanha.dataInicio)) &&
                !agora.isAfter(Instant.parse(campanha.dataFim))
        } catch (e: DateTimeParseException) {
            false
        }
    }

    LaunchedEffect(dashboardScreenViewModel) {
        if (dashboardScreenViewModel.campanhas.value == null) {
            dashboardScreenViewModel.buscarCampanhas()
        }
    }

    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = carregando,
        onRefresh = {
            dashboardScreenViewModel.buscarCampanhas()
        },
        state = state,
        modifier = modifier.fillMaxSize().background(colorResource(R.color.background_cream)),
        indicator = {
            PullToRefreshDefaults.Indicator(
                isRefreshing = carregando,
                state = state,
                modifier = Modifier.align(Alignment.TopCenter),
                color = colorResource(R.color.main_pink),
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 20.dp,
                        ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = colorResource(R.color.main_text_color),
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Campanhas",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.main_text_color),
                    )

                    Text(
                        text = "Participe e divulgue",
                        fontSize = 14.sp,
                        color = colorResource(R.color.secundary_gray),
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding =
                    PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        top = 16.dp,
                        bottom = 32.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when {
                    mensagem != null -> {
                        item {
                            Text(
                                text = mensagem.orEmpty(),
                                color = colorResource(R.color.secundary_gray),
                            )
                        }
                    }

                    campanhasVigentes.isEmpty() && !carregando -> {
                        item {
                            Text(
                                text = "Nenhuma campanha disponível no momento.",
                                color = colorResource(R.color.secundary_gray),
                            )
                        }
                    }

                    else -> {
                        items(
                            items = campanhasVigentes,
                            key = { it.id },
                        ) { campanha ->
                            CampanhaCard(
                                campanha = campanha,
                                modifier = Modifier.fillMaxWidth(),
                                exibirDetalhes = true,
                                onClick = {
                                    navController.navigate("campanha/${campanha.id}")
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
