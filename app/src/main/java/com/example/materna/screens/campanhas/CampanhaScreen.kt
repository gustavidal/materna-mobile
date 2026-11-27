package com.example.materna.screens.campanhas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.materna.R
import com.example.materna.models.responses.CampanhaResponse
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CampanhaScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    campanha: CampanhaResponse,
) {
    val formato =
        DateTimeFormatter.ofPattern(
            "dd MMM",
            Locale("pt", "BR"),
        )

    val zona = ZoneId.of("America/Sao_Paulo")

    val periodo =
        try {
            val inicio = Instant.parse(campanha.dataInicio).atZone(zona).format(formato)

            val fim = Instant.parse(campanha.dataFim).atZone(zona).format(formato)

            "$inicio — $fim".uppercase(Locale("pt", "BR"))
        } catch (e: Exception) {
            "Período indisponível"
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colorResource(R.color.background_cream))
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 24.dp,
                    vertical = 32.dp,
                ),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier =
                    Modifier.background(
                        colorResource(R.color.pink_beige),
                        CircleShape,
                    ),
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
                    text = campanha.titulo,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_text_color),
                    lineHeight = 29.sp,
                )

                Text(
                    text = "Participe e divulgue",
                    fontSize = 14.sp,
                    color = colorResource(R.color.secundary_gray),
                )
            }
        }

        AsyncImage(
            model = campanha.foto,
            contentDescription = campanha.titulo,
            modifier = Modifier.fillMaxWidth().height(230.dp).clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Ativa",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = androidx.compose.ui.graphics.Color(0xFF43A85B),
                modifier =
                    Modifier.background(
                            color = androidx.compose.ui.graphics.Color(0xFFE5F5E9),
                            shape = CircleShape,
                        )
                        .padding(
                            horizontal = 14.dp,
                            vertical = 8.dp,
                        ),
            )

            Row(
                modifier =
                    Modifier.background(
                            colorResource(R.color.pink_beige),
                            CircleShape,
                        )
                        .padding(
                            horizontal = 14.dp,
                            vertical = 8.dp,
                        ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = null,
                    tint = colorResource(R.color.secundary_gray),
                    modifier = Modifier.size(20.dp),
                )

                Text(
                    text = periodo,
                    fontSize = 13.sp,
                    color = colorResource(R.color.main_text_color),
                )
            }
        }

        Text(
            text = campanha.descricao,
            fontSize = 17.sp,
            lineHeight = 28.sp,
            color = colorResource(R.color.secundary_gray),
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
