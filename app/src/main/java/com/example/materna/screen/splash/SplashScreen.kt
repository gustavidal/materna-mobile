package com.example.materna.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun SplashScreen(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.main_pink)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_materna_logo),
                contentDescription = "Logo Materna",
                modifier = Modifier
                    .size(160.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Materna",
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Doe leite.\nCompartilhe esperança",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(54.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(180.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)), // Pontas da barra arredondadas
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.35f)
            )
        }
    }
}