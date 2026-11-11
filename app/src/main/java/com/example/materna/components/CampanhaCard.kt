package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

@Composable
fun CampanhaCard(
    campanha: CampanhaResponse,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(280.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(colorResource(R.color.pink_beige))
    ) {
        AsyncImage(
            model = campanha.foto,
            contentDescription = campanha.titulo,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = campanha.titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.main_text_color),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = campanha.descricao,
                fontSize = 14.sp,
                color = colorResource(R.color.secundary_gray),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
