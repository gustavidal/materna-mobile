package com.example.materna.screens.confirmaremail

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.CodigoVerificacaoField

@Composable
fun ConfirmarEmailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    idDoadora: Int,
    confirmarEmailScreenViewModel: ConfirmarEmailScreenViewModel
) {
    val email               by confirmarEmailScreenViewModel.email.observeAsState("")
    val codigo              by confirmarEmailScreenViewModel.codigo.observeAsState("")

    val confirmando         by confirmarEmailScreenViewModel.confirmando.observeAsState(false)

    val mensagemConfirmacao by confirmarEmailScreenViewModel.mensagemConfirmacao.observeAsState()

    val codigoConfirmado    by confirmarEmailScreenViewModel.codigoConfirmado.observeAsState(false)

    // Navegação após confirmação bem-sucedida
    LaunchedEffect(codigoConfirmado) {
        if (codigoConfirmado) {
            navController.navigate("login") {
                popUpTo("confirmar-email/{id_doadora}") {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Cabeçalho
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("confirmacao/{id_doadora}") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                enabled = !confirmando,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Voltar para o login",
                    tint = colorResource(R.color.main_text_color)
                )
            }

            Text(
                text = "Confirmar E-mail",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.main_text_color)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Ícone de e-mail
        Box(
            modifier = Modifier
                .size(74.dp)
                .shadow(12.dp, CircleShape)
                .background(colorResource(R.color.white), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(colorResource(R.color.pink_beige), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_mail),
                    contentDescription = null,
                    tint = colorResource(R.color.main_pink),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Verifique seu código",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.main_text_color)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Card principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(22.dp)
                )
                .background(
                    colorResource(R.color.white),
                    RoundedCornerShape(22.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Enviamos um código para seu e-mail",
                fontSize = 12.sp,
                color = colorResource(R.color.main_text_color)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card com e-mail cadastrado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorResource(R.color.background_cream))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(colorResource(R.color.white), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mail),
                        contentDescription = null,
                        tint = colorResource(R.color.main_pink),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "E-mail principal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.main_text_color)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = if (email.isNotBlank()) "E-mail enviado para\n$email" else "E-mail não disponível",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = colorResource(R.color.secundary_gray)
                    )
                }

                // Indicador de e-mail
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(colorResource(R.color.main_pink), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        tint = colorResource(R.color.white),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Informações do código
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CÓDIGO RECEBIDO",
                    fontSize = 11.sp,
                    color = colorResource(
                        R.color.main_text_color
                    )
                )

                Text(
                    text = "Válido por 10 min",
                    fontSize = 11.sp,
                    color = colorResource(
                        R.color.secundary_gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Campo componentizado
            CodigoVerificacaoField(
                codigo = codigo,
                onCodigoChanged = {
                    confirmarEmailScreenViewModel.onCodigoChanged(it)
                },
                quantidadeDigitos = 6,
                enabled = !confirmando,
                modifier = Modifier.fillMaxWidth()
            )

            // Mensagem de erro da API
            if (!mensagemConfirmacao.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = mensagemConfirmacao.orEmpty(),
                    color = colorResource(R.color.red),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Botão de confirmação
            Button(
                onClick = {
                    confirmarEmailScreenViewModel.confirmarCodigo(idDoadora)
                },
                enabled = codigo.length == 6 &&
                        email.isNotBlank() &&
                        !confirmando &&
                        !codigoConfirmado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        R.color.main_pink
                    ),
                    disabledContainerColor = colorResource(
                        R.color.main_pink
                    ).copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 5.dp
                )
            ) {
                if (confirmando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = colorResource(R.color.white),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Confirmar e-mail →",
                        color = colorResource(R.color.white),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Rodapé
        Text(
            text = "Já confirmou seu e-mail?",
            fontSize = 12.sp,
            color = colorResource(R.color.main_text_color)
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo("confirmacao/{id_doadora}") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            enabled = !confirmando,
            colors = ButtonDefaults.textButtonColors(
                containerColor = colorResource(R.color.background_gray)
            )
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier
                        .size(20.dp),
                    painter = painterResource(R.drawable.ic_login),
                    tint = colorResource(R.color.main_pink),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = "Fazer login",
                    color = colorResource(R.color.main_pink),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}