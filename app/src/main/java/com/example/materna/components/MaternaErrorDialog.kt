package com.example.materna.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun MaternaErrorDialog(
    titulo: String,
    mensagem: String,
    onTentarNovamente: () -> Unit,
    onVoltar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        shape = RoundedCornerShape(24.dp),
        containerColor = colorResource(R.color.background_cream),
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = colorResource(R.color.main_pink)
            )
        },
        title = {
            Text(
                text = titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.main_text_color)
            )
        },
        text = {
            Text(
                text = mensagem,
                fontSize = 14.sp,
                color = colorResource(R.color.secundary_gray)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onTentarNovamente
            ) {
                Text(
                    text = "Tentar novamente",
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.main_pink)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onVoltar
            ) {
                Text(
                    text = "Voltar",
                    color = colorResource(R.color.secundary_gray)
                )
            }
        }
    )
}
