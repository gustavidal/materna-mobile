package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun CodigoVerificacaoField(
    codigo: String,
    onCodigoChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    quantidadeDigitos: Int = 6,
    enabled: Boolean = true
) {
    require(quantidadeDigitos > 0) {
        "A quantidade de dígitos deve ser maior que zero."
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    BasicTextField(
        value = codigo,
        onValueChange = { novoCodigo ->
            val codigoFiltrado = novoCodigo
                .filter { it in '0'..'9' }
                .take(quantidadeDigitos)

            if (codigoFiltrado != codigo) {
                onCodigoChanged(codigoFiltrado)
            }
        },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = false,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),
        textStyle = TextStyle(
            color = Color.Transparent,
            fontSize = 18.sp
        ),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(
            Color.Transparent
        ),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(quantidadeDigitos) { index ->

                        val digito = codigo.getOrNull(index)

                        val preenchido = digito != null

                        val ativo = enabled &&
                                index == codigo.length &&
                                codigo.length < quantidadeDigitos

                        val destacado = preenchido || ativo

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    color = if (destacado) colorResource(R.color.white) else colorResource(R.color.background_gray),
                                    shape = RoundedCornerShape(11.dp)
                                )
                                .border(
                                    width = if (destacado) {
                                        2.dp
                                    } else {
                                        1.dp
                                    },
                                    color = if (destacado) colorResource(R.color.main_pink) else colorResource(R.color.border),
                                    shape = RoundedCornerShape(11.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digito?.toString()
                                    ?: if (ativo) "" else "•",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (preenchido) colorResource(R.color.main_text_color) else colorResource(R.color.secundary_gray)
                            )
                        }
                    }
                }

                // Mantém o campo real na árvore de layout, permitindo que o BasicTextField controle foco, teclado, seleção e colagem.
                Box(
                    modifier = Modifier
                        .matchParentSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    innerTextField()
                }
            }
        }
    )
}