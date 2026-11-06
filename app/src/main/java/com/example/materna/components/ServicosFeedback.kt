package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun ServicosFeedback(
    titulo: String,
    descricao: String,
    modifier: Modifier = Modifier,
    onTentarNovamente: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_wifi_off),
            contentDescription = null,
            tint = colorResource(R.color.main_pink),
            modifier = Modifier
                .background(
                    color = Color(0xFFFFEFF3),
                    shape = CircleShape
                )
                .padding(18.dp)
                .size(38.dp)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = titulo,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.main_text_color),
            textAlign = TextAlign.Center
        )

        Text(
            text = descricao,
            fontSize = 13.sp,
            color = colorResource(R.color.secundary_gray),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        if (onTentarNovamente != null) {
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onTentarNovamente,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.main_pink)
                )
            ) {
                Text(
                    text = "Tentar novamente",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
