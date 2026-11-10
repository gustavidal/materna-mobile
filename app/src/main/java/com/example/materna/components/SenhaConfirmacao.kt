package com.example.materna.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.materna.R
import com.example.materna.utils.ErroCampos
import com.example.materna.utils.ValidadorCampos

@Composable
fun SenhaConfirmacao(
    senha: String,
    confirmacao: String,
    campoVisitado: Boolean
) {
    val senhasCoincidem = ValidadorCampos.senhasCoincidem(
        senha,
        confirmacao
    )

    val erro = ErroCampos.confirmarSenha(
        senha,
        confirmacao
    )

    AnimatedVisibility(
        visible = senhasCoincidem ||
                (campoVisitado && erro != null),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Text(
            text = if (senhasCoincidem) {
                "Senhas coincidem"
            } else {
                erro.orEmpty()
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (senhasCoincidem) {
                colorResource(R.color.green)
            } else {
                colorResource(R.color.red)
            }
        )
    }
}
