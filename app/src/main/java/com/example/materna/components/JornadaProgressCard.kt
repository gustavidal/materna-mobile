package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun JornadaProgressCard(
    progresso: Int,
    modifier: Modifier = Modifier
) {
    val progressoValidado = progresso.coerceIn(0, 100)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(R.color.main_pink),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Progresso",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Text(
                text = "$progressoValidado%",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LinearProgressIndicator(
            progress = { progressoValidado / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.3f),
            gapSize = 0.dp,
            drawStopIndicator = {}
        )
    }
}