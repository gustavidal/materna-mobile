package com.example.materna.screens.cadastro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.CadastroField
import com.example.materna.components.CadastroStepIndicator
import kotlinx.coroutines.delay

private const val LAST_STEP = 2

@Composable
fun CadastroScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    cadastroScreenViewModel: CadastroScreenViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val currentStep        by cadastroScreenViewModel.currentStep.observeAsState(0)

    val nome               by cadastroScreenViewModel.nome.observeAsState("")
    val dataNascimento     by cadastroScreenViewModel.dataNascimento.observeAsState("")
    val cpf                by cadastroScreenViewModel.cpf.observeAsState("")
    val telefone           by cadastroScreenViewModel.telefone.observeAsState("")

    val cep                by cadastroScreenViewModel.cep.observeAsState("")
    val estado             by cadastroScreenViewModel.estado.observeAsState("")
    val cidade             by cadastroScreenViewModel.cidade.observeAsState("")
    val logradouro         by cadastroScreenViewModel.logradouro.observeAsState("")
    val bairro             by cadastroScreenViewModel.bairro.observeAsState("")
    val numero             by cadastroScreenViewModel.numero.observeAsState(null)
    val complemento        by cadastroScreenViewModel.complemento.observeAsState("")

    val email              by cadastroScreenViewModel.email.observeAsState("")
    val senha              by cadastroScreenViewModel.senha.observeAsState("")
    val confirmarSenha     by cadastroScreenViewModel.confirmarSenha.observeAsState("")
    val termosAceitos      by cadastroScreenViewModel.termosAceitos.observeAsState(false)

    val mostrarSenha       by cadastroScreenViewModel.mostrarSenha.observeAsState(false)
    val mostrarConfirmacao by cadastroScreenViewModel.mostrarConfirmacao.observeAsState(false)

    val cadastrando        by cadastroScreenViewModel.cadastrando.observeAsState()
    val mensagemCadastro   by cadastroScreenViewModel.mensagemCadastro.observeAsState()
    val cadastroConcluido  by cadastroScreenViewModel.cadastroConcluido.observeAsState()

    LaunchedEffect(mostrarSenha) {
        if (mostrarSenha) {
            delay(3000)
            cadastroScreenViewModel.ocultarSenha()
        }
    }

    LaunchedEffect(mostrarConfirmacao) {
        if (mostrarConfirmacao) {
            delay(3000)
            cadastroScreenViewModel.ocultarConfirmacao()
        }
    }

    val consultandoCep     by cadastroScreenViewModel.consultandoCep.observeAsState(false)
    val erroCep            by cadastroScreenViewModel.erroCep.observeAsState(null)
    val erroDataNascimento =  cadastroScreenViewModel.erroDataNascimento()
    val erroCpf            =  cadastroScreenViewModel.erroCpf()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream))
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                    }
                )
            }
        ) {
//        -- cabeçalho
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.background_gray))
                    .clickable {
                        if (currentStep > 0) {
                            cadastroScreenViewModel.voltarEtapa()
                        } else {
                            navController.navigate("login") {
                                launchSingleTop = true
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    fontSize = 34.sp,
                    color = colorResource(R.color.main_text_color),
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "Etapa ${currentStep + 1} de 3",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFF5EFEB))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 13.sp,
                color = colorResource(R.color.secundary_gray)
            )
        }

        Spacer(Modifier.height(24.dp))

//        -- indicadores animados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CadastroStepIndicator(
                currentStep = currentStep,
                totalSteps = 3
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Criar conta",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.main_text_color)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Preencha seus dados pessoais",
            fontSize = 15.sp,
            color = colorResource(R.color.secundary_gray)
        )

        // Área rolável: somente os campos mudam entre etapas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (currentStep) {
                0 -> {
                    CadastroField(
                        label = "Nome completo",
                        value = nome,
                        onValueChange = { cadastroScreenViewModel.onNomeChanged(it) },
                        placeholder = "Maria Silva",
                        icon = painterResource(R.drawable.ic_profile)
                    )



                    CadastroField(
                        label = "Data de nascimento",
                        value = dataNascimento,
                        onValueChange = { cadastroScreenViewModel.onDataNascimentoChanged(it) },
                        placeholder = "dd/mm/aaaa",
                        icon = painterResource(R.drawable.ic_calendar),
                        keyboardType = KeyboardType.Number
                    )
                    if (erroDataNascimento != null) {
                        Text(
                            text = erroDataNascimento,
                            fontSize = 12.sp,
                            color = colorResource(R.color.red)
                        )
                    }

                    CadastroField(
                        label = "CPF",
                        value = cpf,
                        onValueChange = { cadastroScreenViewModel.onCpfChanged(it) },
                        placeholder = "000.000.000-00",
                        icon = painterResource(R.drawable.ic_document),
                        keyboardType = KeyboardType.Number
                    )

                    if (erroCpf != null) {
                        Text(
                            text = erroCpf,
                            fontSize = 12.sp,
                            color = colorResource(R.color.red)
                        )
                    }

                    CadastroField(
                        label = "Telefone",
                        value = telefone,
                        onValueChange = { cadastroScreenViewModel.onTelefoneChanged(it) },
                        placeholder = "(11) 90000-0000",
                        icon = painterResource(R.drawable.ic_phone),
                        keyboardType = KeyboardType.Phone
                    )
                }

                1 -> {
                    CadastroField(
                        label = "CEP",
                        value = cep,
                        onValueChange = { cadastroScreenViewModel.onCepChanged(it) },
                        placeholder = "00000-000",
                        icon = painterResource(R.drawable.ic_pin_location),
                        keyboardType = KeyboardType.Number
                    )

                    if (consultandoCep) {
                        Text(
                            text = "Consultando CEP...",
                            fontSize = 12.sp,
                            color = colorResource(R.color.secundary_gray)
                        )
                    }

                    if (erroCep != null) {
                        Text(
                            text = erroCep.orEmpty(),
                            fontSize = 12.sp,
                            color = Color.Red
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(0.8f)) {
                            CadastroField(
                                label = "Estado",
                                value = estado,
                                onValueChange = { cadastroScreenViewModel.onEstadoChanged(it) },
                                placeholder = "UF",
                                enabled = false
                            )
                        }

                        Box(Modifier.weight(1.6f)) {
                            CadastroField(
                                label = "Cidade",
                                value = cidade,
                                onValueChange = { cadastroScreenViewModel.onCidadeChanged(it) },
                                placeholder = "Sua cidade",
                                icon = painterResource(R.drawable.ic_building),
                                enabled = false
                            )
                        }
                    }

                    CadastroField(
                        label = "Logradouro",
                        value = logradouro,
                        onValueChange = { cadastroScreenViewModel.onLogradouroChanged(it) },
                        placeholder = "Rua, Avenida, etc.",
                        icon = painterResource(R.drawable.ic_house),
                        enabled = false
                    )

                    CadastroField(
                        label = "Bairro",
                        value = bairro,
                        onValueChange = { cadastroScreenViewModel.onBairroChanged(it) },
                        placeholder = "Seu bairro",
                        icon = painterResource(R.drawable.ic_neighborhood),
                        enabled = false
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(0.8f)) {
                            CadastroField(
                                label = "Número",
                                value = if (numero != null) "$numero" else "",
                                onValueChange = { cadastroScreenViewModel.onNumeroChanged(it.toIntOrNull()) },
                                placeholder = "123",
                                keyboardType = KeyboardType.Number
                            )
                        }

                        Box(Modifier.weight(1.6f)) {
                            CadastroField(
                                label = "Complemento",
                                value = complemento,
                                onValueChange = { cadastroScreenViewModel.onComplementoChanged(it) },
                                placeholder = "Apto, Bloco, etc."
                            )
                        }
                    }
                }

                2 -> {
                    CadastroField(
                        label = "E-mail",
                        value = email,
                        onValueChange = { cadastroScreenViewModel.onEmailChanged(it) },
                        placeholder = "seu@email.com",
                        icon = painterResource(R.drawable.ic_email),
                        keyboardType = KeyboardType.Email
                    )

                    CadastroField(
                        label = "Senha",
                        value = senha,
                        onValueChange = { cadastroScreenViewModel.onSenhaChanged(it) },
                        placeholder = "Digite sua senha",
                        icon = painterResource(R.drawable.ic_lock),
                        isPassword = !mostrarSenha,
                        trailingIcon = if (mostrarSenha) painterResource(R.drawable.ic_visibility_off) else painterResource(R.drawable.ic_visibility_on),
                        onTrailingClick = {
                            cadastroScreenViewModel.toggleMostrarSenha()
                        },
                        keyboardType = KeyboardType.Password
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val senhaValida =
                            cadastroScreenViewModel.senhaTemOitoCaracteres() &&
                                    cadastroScreenViewModel.senhaTemMaiuscula() &&
                                    cadastroScreenViewModel.senhaTemNumero() &&
                                    cadastroScreenViewModel.senhaTemCaractereEspecial()

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

                        AnimatedVisibility(
                            visible = !senhaValida &&
                                    !cadastroScreenViewModel.senhaTemOitoCaracteres(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = "- Mínimo de 8 caracteres",
                                fontSize = 12.sp,
                                color = colorResource(R.color.secundary_gray)
                            )
                        }

                        AnimatedVisibility(
                            visible = !senhaValida &&
                                    !cadastroScreenViewModel.senhaTemMaiuscula(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = "- 1 letra maiúscula",
                                fontSize = 12.sp,
                                color = colorResource(R.color.secundary_gray)
                            )
                        }

                        AnimatedVisibility(
                            visible = !senhaValida &&
                                    !cadastroScreenViewModel.senhaTemNumero(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = "- 1 número",
                                fontSize = 12.sp,
                                color = colorResource(R.color.secundary_gray)
                            )
                        }

                        AnimatedVisibility(
                            visible = !senhaValida &&
                                    !cadastroScreenViewModel.senhaTemCaractereEspecial(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = "- 1 caractere especial",
                                fontSize = 12.sp,
                                color = colorResource(R.color.secundary_gray)
                            )
                        }

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

                    CadastroField(
                        label = "Confirmar senha",
                        value = confirmarSenha,
                        onValueChange = { cadastroScreenViewModel.onConfirmarSenhaChanged(it) },
                        placeholder = "Confirme sua senha",
                        icon = painterResource(R.drawable.ic_lock),
                        isPassword = !mostrarConfirmacao,
                        trailingIcon = if (mostrarConfirmacao) painterResource(R.drawable.ic_visibility_off) else painterResource(R.drawable.ic_visibility_on),
                        onTrailingClick = {
                            cadastroScreenViewModel.toggleMostrarConfirmacao()
                        },
                        keyboardType = KeyboardType.Password
                    )

                    val senhasCoincidem = confirmarSenha.isNotEmpty() &&
                            confirmarSenha == senha

                    AnimatedVisibility(
                        visible = confirmarSenha.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = if (senhasCoincidem) "Senhas coincidem" else "As senhas não coincidem",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (senhasCoincidem) colorResource(R.color.green) else colorResource(R.color.red)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termosAceitos,
                            onCheckedChange = { cadastroScreenViewModel.onTermosAceitosChanged(it) }
                        )

                        Text(
                            text = "Li e concordo com os Termos de Uso e a Política de Privacidade.",
                            fontSize = 12.sp,
                            color = colorResource(R.color.secundary_gray)
                        )
                    }
                }
            }
        }

        if (mensagemCadastro != null) {
            Text(
                text = mensagemCadastro.orEmpty(),
                color = if (cadastroConcluido == true) {
                    Color(0xFF2E7D32)
                } else {
                    Color.Red
                },
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(20.dp))

        // Rodapé fixo
        Button(
            onClick = {
                if (currentStep < LAST_STEP) {
                    cadastroScreenViewModel.avancarEtapa()
                } else {
                    cadastroScreenViewModel.cadastrarDoadora()
                }

                focusManager.clearFocus()
                keyboardController?.hide()
            },
            enabled = cadastroScreenViewModel.podeContinuar() && cadastrando != true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.main_pink),
                disabledContainerColor = colorResource(R.color.border)
            ),

        ) {
            Text(
                text = when {
                    cadastrando == true -> "Cadastrando..."
                    currentStep == LAST_STEP -> "Finalizar cadastro"
                    else -> "Continuar"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Já tem uma conta? ",
                fontSize = 13.sp,
                color = colorResource(R.color.secundary_gray)
            )

            Text(
                text = "Entrar",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.main_pink),
                modifier = Modifier.clickable {
                    navController.navigate("login") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}