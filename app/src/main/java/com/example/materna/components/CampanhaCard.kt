package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.materna.R
import com.example.materna.models.responses.CampanhaResponse
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CampanhaCard(
    campanha: CampanhaResponse,
    modifier: Modifier = Modifier,
    exibirDetalhes: Boolean = false,
    onClick: () -> Unit
) {
    val formato = DateTimeFormatter.ofPattern(
        "dd/MM", Locale("pt", "BR")
    )

    val zona = ZoneId.of("America/Sao_Paulo")

    val periodo = try {
        val inicio = Instant.parse(campanha.dataInicio)
            .atZone(zona)
            .format(formato)

        val fim = Instant.parse(campanha.dataFim)
            .atZone(zona)
            .format(formato)

        "$inicio – $fim"
    } catch (e: Exception) {
        "Período indisponível"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colorResource(R.color.white))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = campanha.foto,
            contentDescription = campanha.titulo,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (exibirDetalhes) {
                Text(
                    text = "Ativa",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.main_text_color),
                    modifier = Modifier
                        .background(
                            color = colorResource(R.color.pink_beige),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 5.dp
                        )
                )
            }

            Text(
                text = campanha.titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.main_text_color),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (exibirDetalhes) {
                Text(
                    text = periodo,
                    fontSize = 13.sp,
                    color = colorResource(R.color.secundary_gray)
                )

                Text(
                    text = "Saiba mais →",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.main_pink)
                )
            }
        }
    }
}