
package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun TipoAtendimentoCard(
    titulo: String,
    descricao: String,
    icone: Painter,
    selecionado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(112.dp)
            .shadow(2.dp, shape)
            .clip(shape)
            .background(colorResource(R.color.white))
            .border(
                width = if (selecionado) 2.dp else 1.dp,
                color = if (selecionado) colorResource(R.color.main_pink) else colorResource(R.color.border),
                shape = shape
            )
            .clickable { onClick() }
    ) {
        if (selecionado) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.main_pink))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (selecionado) colorResource(R.color.pink_beige) else colorResource(R.color.background_gray)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icone,
                    contentDescription = null,
                    tint = if (selecionado) colorResource(R.color.main_pink) else colorResource(R.color.secundary_gray),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = titulo,
                fontSize = 14.sp,
                fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal,
                color = colorResource(R.color.main_text_color)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = descricao,
                fontSize = 11.sp,
                color = colorResource(R.color.secundary_gray)
            )
        }
    }
}
