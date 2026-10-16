package com.example.materna.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.OnboardingCard

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var currentStep by remember {
        mutableStateOf(0)
    }

    val images = listOf(
        R.drawable.img_onboarding1,
        R.drawable.img_onboarding2,
        R.drawable.img_onboarding3
    )

    val titles = listOf(
        "Sua doação pode ajudar",
        "Entenda cada etapa",
        "Encontre um serviço"
    )

    val descriptions = listOf(
        "O leite humano é essencial para bebês prematuros. Uma única doação pode alimentar vários recém-nascidos e salvar vidas.",
        "O aplicativo acompanha você durante todo o processo, com orientações claras sobre higiene, coleta e armazenamento.",
        "Localize bancos de leite e serviços de coleta próximos de você em poucos toques."
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
    ) {
        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo("onboarding") { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.background),
                contentColor = colorResource(R.color.secundary_gray)
            ),
        ) {
            Text(
                text = "Pular",
                fontSize = 14.sp
            )
        }

        OnboardingCard(
            imageRes = images[currentStep],
            title = titles[currentStep],
            description = descriptions[currentStep],
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.height(40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { iteration ->
                    val isSelected = currentStep == iteration
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFFC9676F) else Color(0xFFE0E0E0))
                            .size(width = if (isSelected) 18.dp else 8.dp, height = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (currentStep < 2) {
                        currentStep++
                    } else {
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC9676F)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (currentStep == 2) "Começar" else "Continuar",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
