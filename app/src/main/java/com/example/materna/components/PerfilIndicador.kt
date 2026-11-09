package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PerfilIndicador(
    valor: String,
    descricao: String,
    cor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                cor.copy(alpha = 0.09f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = valor,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = cor
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = descricao,
            fontSize = 11.sp,
            color = cor,
            lineHeight = 14.sp,
            minLines = 2,
            textAlign = TextAlign.Center
        )
    }
}