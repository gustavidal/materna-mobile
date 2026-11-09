package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun PerfilOpcao(
    titulo: String,
    simbolo: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    colorResource(R.color.main_pink).copy(alpha = 0.10f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = simbolo,
                fontSize = 23.sp,
                color = colorResource(R.color.main_pink)
            )
        }

        Text(
            text = titulo,
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.main_text_color)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colorResource(R.color.secundary_gray)
        )
    }
}