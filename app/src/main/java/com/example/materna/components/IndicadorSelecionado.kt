package com.example.materna.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.materna.R

@Composable
fun IndicadorSelecionado(
    selecionado: Boolean
) {
    RadioButton(
        selected = selecionado,
        onClick = null,
        colors = RadioButtonDefaults.colors(
            selectedColor = colorResource(R.color.main_pink),
            unselectedColor = colorResource(R.color.border)
        ),
        modifier = Modifier.size(25.dp)
    )
}