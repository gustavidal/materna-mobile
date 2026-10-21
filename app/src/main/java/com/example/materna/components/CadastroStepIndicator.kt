package com.example.materna.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.materna.R

@Composable
fun CadastroStepIndicator(
    currentStep: Int,
    totalSteps: Int = 3
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalSteps) { index ->
            val indicatorWidth by animateFloatAsState(
                targetValue = if (index <= currentStep) 1f else 0.45f,
                animationSpec = tween(250),
                label = "stepIndicator$index"
            )

            Box(
                modifier = Modifier
                    .weight(indicatorWidth)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (index <= currentStep) {
                            colorResource(R.color.main_pink)
                        } else {
                            colorResource(R.color.border)
                        }
                    )
            )
        }
    }
}
