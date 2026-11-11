package com.example.materna.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.LoginField
import kotlinx.coroutines.delay

val FieldShape = RoundedCornerShape(28.dp)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    loginScreenViewModel: LoginScreenViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val email        by loginScreenViewModel.email.observeAsState(initial = "")
    val senha        by loginScreenViewModel.senha.observeAsState(initial = "")

    val mostrarSenha by loginScreenViewModel.mostrarSenha.observeAsState(false)

    LaunchedEffect(mostrarSenha) {
        if (mostrarSenha) {
            delay(3000)
            loginScreenViewModel.ocultarSenha()
        }
    }

    val textColor       = colorResource(R.color.main_text_color)
    val secondaryColor  = colorResource(R.color.secundary_gray)
    val borderColor     = colorResource(R.color.border)
    val pinkColor       = colorResource(R.color.main_pink)
    val backgroundColor = colorResource(R.color.background_cream)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                    }
                )
            }
            .padding(horizontal = 28.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
//        -- cabeçalho
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_materna_logo_sem_fundo),
                contentDescription = "Logo Materna",
                modifier = Modifier
                    .size(120.dp)
            )

            Text(
                text = "Materna",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Text(
                text = "Bem-vinda",
                fontSize = 13.sp,
                color = secondaryColor
            )
        }

//        -- seção de formulário
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "E-mail",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            LoginField(
                value = email,
                onValueChange = loginScreenViewModel::onEmailChanged,
                placeholder = "Digite seu e-mail",
                iconRes = R.drawable.ic_mail,
                borderColor = borderColor,
                textColor = textColor,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Senha",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            LoginField(
                value = senha,
                onValueChange = loginScreenViewModel::onSenhaChanged,
                placeholder = "Digite sua senha",
                iconRes = R.drawable.ic_lock,
                borderColor = borderColor,
                textColor = textColor,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = mostrarSenha,
                onTogglePassword = {
                    loginScreenViewModel.toggleMostrarSenha()
                }
            )

            Text(
                text = "Esqueci minha senha",
                fontSize = 14.sp,
                color = pinkColor,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 10.dp)
                    .clickable(
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null
                    ) {

                    }
            )

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = pinkColor
                )
            ) {
                Text(
                    text = "Entrar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

//        -- seção de um novo cadastro
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "Ainda não tenho conta",
                fontSize = 16.sp,
                color = secondaryColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    navController.navigate("cadastro")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .border(
                        width = 2.dp,
                        color = borderColor,
                        shape = CircleShape
                    ),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = textColor
                )
            ) {
                Text(
                    text = "Criar conta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}