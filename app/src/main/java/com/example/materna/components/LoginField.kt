package com.example.materna.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import com.example.materna.screens.login.FieldShape

@Composable
fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    iconRes: Int,
    borderColor: Color,
    textColor: Color,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    val shape = FieldShape

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(shape)
            .background(Color.White)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = shape
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = colorResource(R.color.secundary_gray)
        )

        Spacer(modifier = Modifier.size(14.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 15.sp,
                    color = Color(0xFF9A8B87)
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = 15.sp
                ),
                cursorBrush = SolidColor(textColor),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType
                ),
                visualTransformation = if (
                    isPassword && !passwordVisible
                ) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isPassword && onTogglePassword != null) {
            IconButton(
                onClick = onTogglePassword,
                modifier = Modifier
                    .size(25.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (passwordVisible) {
                            R.drawable.ic_visibility_off
                        } else {
                            R.drawable.ic_visibility_on
                        }
                    ),
                    contentDescription = if (passwordVisible) {
                        "Ocultar senha"
                    } else {
                        "Mostrar senha"
                    },
                    tint = borderColor
                )
            }
        }
    }
}