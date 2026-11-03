package com.example.materna.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun EmDesenvolvimentoScreen(
    titulo: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = titulo,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.main_text_color)
        )

        Text(
            text = "Em desenvolvimento",
            fontSize = 16.sp,
            color = colorResource(R.color.secundary_gray)
        )
    }
}
