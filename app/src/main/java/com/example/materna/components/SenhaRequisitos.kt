package com.example.materna.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import com.example.materna.utils.ValidadorCampos

@Composable
fun SenhaRequisitos(
    senha: String
) {
    val senhaValida = ValidadorCampos.senhaValida(senha)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AnimatedVisibility(
            visible = !senhaValida,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = "A senha ainda precisa de:",
                fontSize = 12.sp,
                color = colorResource(R.color.secundary_gray)
            )
        }

        RequisitoSenha(
            texto = "- Mínimo de 8 caracteres",
            visivel = !senhaValida && !ValidadorCampos.senhaTemOitoCaracteres(senha)
        )

        RequisitoSenha(
            texto = "- 1 letra maiúscula",
            visivel = !senhaValida && !ValidadorCampos.senhaTemMaiuscula(senha)
        )

        RequisitoSenha(
            texto = "- 1 número",
            visivel = !senhaValida && !ValidadorCampos.senhaTemNumero(senha)
        )

        RequisitoSenha(
            texto = "- 1 caractere especial",
            visivel = !senhaValida && !ValidadorCampos.senhaTemCaractereEspecial(senha)
        )

        AnimatedVisibility(
            visible = senhaValida,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = "Senha válida",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(R.color.green)
            )
        }
    }
}

@Composable
private fun RequisitoSenha(
    texto: String,
    visivel: Boolean
) {
    AnimatedVisibility(
        visible = visivel,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Text(
            text = texto,
            fontSize = 12.sp,
            color = colorResource(R.color.secundary_gray)
        )
    }
}
