package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R

@Composable
fun CadastroField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    isPassword: Boolean = false,
    trailingIcon: Painter? = null,
    onTrailingClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    maxLength: Int? = null,
    onFocusLost: (() -> Unit)? = null
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(value, selection = TextRange(value.length))
        )
    }

    var recebeuFoco by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            textFieldValue = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        }
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.main_text_color),
            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(if (enabled) colorResource(R.color.white) else colorResource(R.color.disabled))
                .border(
                    width = 1.5.dp,
                    color = colorResource(R.color.border),
                    shape = RoundedCornerShape(17.dp)
                )
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = "",
                    tint = colorResource(R.color.secundary_gray),
                    modifier = Modifier
                        .size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
            }

            Box(Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = Color(0xFFB8B8C3)
                    )
                }

                BasicTextField(
                    value = textFieldValue.copy(text = value),
                    onValueChange = { newValue ->
                        if (enabled &&
                            (maxLength == null || newValue.text.length <= maxLength)
                        ) {
                            val newText = newValue.text

                            val addedSlash =
                                newText.length > value.length &&
                                        newText.contains("/") &&
                                        !value.contains("/") &&
                                        newText.indexOf("/") == 2

                            val cursorPosition = if (addedSlash) {
                                (newValue.selection.start + 1).coerceAtMost(newText.length)
                            } else {
                                newValue.selection.start
                            }

                            textFieldValue = newValue.copy(
                                selection = TextRange(cursorPosition)
                            )

                            onValueChange(newText)
                        }
                    },
                    enabled = enabled,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = colorResource(R.color.main_text_color)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        capitalization = capitalization
                    ),
                    visualTransformation = if (isPassword) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                recebeuFoco = true
                            } else if (recebeuFoco) {
                                recebeuFoco = false
                                onFocusLost?.invoke()
                            }
                        }
                )
            }

            if (trailingIcon != null && onTrailingClick != null) {
                Spacer(Modifier.width(6.dp))

                IconButton(
                    onClick = onTrailingClick,
                    modifier = Modifier
                        .size(25.dp)
                ) {
                    Icon(
                        painter = trailingIcon,
                        contentDescription = if (isPassword) {
                            "Ocultar senha"
                        } else {
                            "Mostrar senha"
                        },
                        tint = colorResource(R.color.border)
                    )
                }
            }
        }
    }
}