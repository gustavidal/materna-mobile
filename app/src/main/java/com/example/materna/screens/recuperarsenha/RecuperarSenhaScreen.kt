
package com.example.materna.screens.recuperarsenha

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.materna.R
import com.example.materna.components.IndicadorSelecionado
import com.example.materna.utils.FormatadorCampos.mascararEmail

@Composable
fun RecuperarSenhaScreen(
    emailCadastrado: String?,
    onVoltarLogin: () -> Unit,
    onEnviarCodigo: (String) -> Unit
) {
    var emailDigitado by rememberSaveable {
        mutableStateOf("")
    }

    var usarOutroEmail by rememberSaveable {
        mutableStateOf(false)
    }

    val emailSalvo = emailCadastrado?.trim().orEmpty()

    val possuiEmailSalvo = emailSalvo.isNotBlank()

    val outroEmailSelecionado =
        usarOutroEmail || !possuiEmailSalvo

    val emailSelecionado = if (outroEmailSelecionado) {
        emailDigitado.trim()
    } else {
        emailSalvo
    }

    val podeEnviar = Patterns.EMAIL_ADDRESS
        .matcher(emailSelecionado)
        .matches()

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_gray))
            .imePadding()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(32.dp))
                .background(colorResource(R.color.background_cream))
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 22.dp,
                    vertical = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Recuperar Senha",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.main_text_color),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // Ícone principal
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .shadow(
                        elevation = 18.dp,
                        shape = CircleShape,
                        ambientColor = colorResource(R.color.pink_beige),
                        spotColor = colorResource(R.color.main_pink)
                    )
                    .background(
                        colorResource(R.color.white),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(
                            colorResource(R.color.pink_beige),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_locked_shield
                        ),
                        contentDescription = null,
                        tint = colorResource(R.color.main_pink),
                        modifier = Modifier.size(39.dp)
                    )
                }
            }

            Spacer(Modifier.height(34.dp))

            Text(
                text = "Esqueceu sua senha?",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.main_text_color),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // Card de recuperação
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(colorResource(R.color.white))
                    .padding(18.dp)
            ) {

                Text(
                    text = "Onde prefere receber o código?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.main_text_color)
                )

                Spacer(Modifier.height(24.dp))

                // Opção 1: E-mail salvo no Room
                if (possuiEmailSalvo) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (!outroEmailSelecionado) {
                                    colorResource(R.color.pink_beige)
                                } else {
                                    colorResource(R.color.background_cream)
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (!outroEmailSelecionado) {
                                    colorResource(R.color.main_pink)
                                } else {
                                    colorResource(R.color.border)
                                },
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                usarOutroEmail = false
                                focusManager.clearFocus()
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    colorResource(R.color.white),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    R.drawable.ic_mail
                                ),
                                contentDescription = null,
                                tint = colorResource(R.color.main_pink),
                                modifier = Modifier.size(25.dp)
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "E-mail principal",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorResource(
                                    R.color.main_text_color
                                )
                            )

                            Spacer(Modifier.height(3.dp))

                            Text(
                                text = mascararEmail(emailSalvo),
                                fontSize = 14.sp,
                                color = colorResource(
                                    R.color.secundary_gray
                                )
                            )

                            Spacer(Modifier.height(3.dp))

                            Text(
                                text = "Conta salva neste dispositivo",
                                fontSize = 12.sp,
                                color = colorResource(
                                    R.color.secundary_gray
                                )
                            )
                        }

                        IndicadorSelecionado(
                            selecionado = !outroEmailSelecionado
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }

                // Opção 2: Outro e-mail
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (outroEmailSelecionado) {
                                colorResource(R.color.pink_beige)
                            } else {
                                colorResource(R.color.background_cream)
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (outroEmailSelecionado) {
                                colorResource(R.color.main_pink)
                            } else {
                                colorResource(R.color.border)
                            },
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            usarOutroEmail = true
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                colorResource(R.color.white),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = colorResource(R.color.main_pink),
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Usar outro e-mail",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(
                                R.color.main_text_color
                            )
                        )

                        Spacer(Modifier.height(3.dp))

                        Text(
                            text = "Recuperar outra conta",
                            fontSize = 12.sp,
                            color = colorResource(
                                R.color.secundary_gray
                            )
                        )
                    }

                    IndicadorSelecionado(
                        selecionado = outroEmailSelecionado
                    )
                }

                // Campo exibido somente para outra conta
                AnimatedVisibility(
                    visible = outroEmailSelecionado
                ) {
                    Column {
                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = "Informe o e-mail da conta",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(
                                R.color.main_text_color
                            )
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = emailDigitado,
                            onValueChange = {
                                emailDigitado = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Digite o e-mail",
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.ic_mail
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(25.dp)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(
                                    R.color.main_pink
                                ),
                                unfocusedBorderColor = colorResource(
                                    R.color.border
                                ),
                                focusedLeadingIconColor = colorResource(
                                    R.color.main_pink
                                ),
                                unfocusedLeadingIconColor = colorResource(
                                    R.color.secundary_gray
                                ),
                                focusedContainerColor = colorResource(
                                    R.color.white
                                ),
                                unfocusedContainerColor = colorResource(
                                    R.color.white
                                )
                            )
                        )

                        if (
                            emailDigitado.isNotBlank() &&
                            !Patterns.EMAIL_ADDRESS
                                .matcher(emailDigitado.trim())
                                .matches()
                        ) {
                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "Informe um e-mail válido.",
                                fontSize = 12.sp,
                                color = colorResource(R.color.main_pink)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Botão de envio
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onEnviarCodigo(emailSelecionado)
                    },
                    enabled = podeEnviar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(
                            R.color.main_pink
                        ),
                        contentColor = colorResource(
                            R.color.white
                        ),
                        disabledContainerColor = colorResource(
                            R.color.disabled
                        ),
                        disabledContentColor = colorResource(
                            R.color.secundary_gray
                        )
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 7.dp
                    )
                ) {
                    Text(
                        text = "Enviar código",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.width(10.dp))

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            // Rodapé
            Text(
                text = "Lembrou a sua senha?",
                fontSize = 16.sp,
                color = colorResource(R.color.main_text_color)
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        colorResource(R.color.background_gray)
                    )
                    .clickable {
                        onVoltarLogin()
                    }
                    .padding(
                        horizontal = 20.dp,
                        vertical = 13.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_login),
                    contentDescription = null,
                    tint = colorResource(R.color.main_pink),
                    modifier = Modifier.size(21.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = "Fazer login",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.main_pink)
                )
            }
        }
    }
}

