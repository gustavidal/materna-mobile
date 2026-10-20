
package com.example.materna.screens.cadastro

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.materna.R

private const val LAST_STEP = 2

private val CadastroPink = Color(0xFFE16B75)
private val CadastroText = Color(0xFF39313B)
private val CadastroGray = Color(0xFF999AAA)
private val CadastroBorder = Color(0xFFF0E0DC)
private val CadastroBackground = Color(0xFFFFF9F6)

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
    val endereco           by cadastroScreenViewModel.endereco.observeAsState("")
    val numero             by cadastroScreenViewModel.numero.observeAsState(null)
    val complemento        by cadastroScreenViewModel.complemento.observeAsState("")

    val email              by cadastroScreenViewModel.email.observeAsState("")
    val senha              by cadastroScreenViewModel.senha.observeAsState("")
    val confirmarSenha     by cadastroScreenViewModel.confirmarSenha.observeAsState("")
    val termosAceitos      by cadastroScreenViewModel.termosAceitos.observeAsState(false)

    val mostrarSenha       by cadastroScreenViewModel.mostrarSenha.observeAsState(false)
    val mostrarConfirmacao by cadastroScreenViewModel.mostrarConfirmacao.observeAsState(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CadastroBackground)
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
                    .background(Color(0xFFF5EFEB))
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
                    color = CadastroText
                )
            }

            Text(
                text = "Etapa ${currentStep + 1} de 3",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFF5EFEB))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 13.sp,
                color = CadastroGray
            )
        }

        Spacer(Modifier.height(24.dp))

//        -- indicadores animados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                val indicatorWidth by animateFloatAsState(
                    targetValue = if (index <= currentStep) 1f else 0.45f,
                    animationSpec = tween(250),
                    label = "stepIndicator$index"
                )

                Box(
                    modifier = Modifier
                        .weight(indicatorWidth)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= currentStep) {
                                colorResource(R.color.main_pink)
                            } else {
                                colorResource(R.color.border)
                            }
                        )
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Criar conta",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = CadastroText
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Preencha seus dados pessoais",
            fontSize = 15.sp,
            color = CadastroGray
        )

        // Área rolável: somente os campos mudam entre etapas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 28.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (currentStep) {
                0 -> {
                    CadastroField(
                        label = "Nome completo",
                        value = nome,
                        onValueChange = { cadastroScreenViewModel.onNomeChanged(it) },
                        placeholder = "Maria Silva",
                        icon = "♙"
                    )

                    CadastroField(
                        label = "Data de nascimento",
                        value = dataNascimento,
                        onValueChange = { cadastroScreenViewModel.onDataNascimentoChanged(it) },
                        placeholder = "dd/mm/aaaa",
                        icon = "▦",
                        keyboardType = KeyboardType.Number
                    )

                    CadastroField(
                        label = "CPF",
                        value = cpf,
                        onValueChange = { cadastroScreenViewModel.onCpfChanged(it) },
                        placeholder = "000.000.000-00",
                        icon = "◷",
                        keyboardType = KeyboardType.Number
                    )

                    CadastroField(
                        label = "Telefone",
                        value = telefone,
                        onValueChange = { cadastroScreenViewModel.onTelefoneChanged(it) },
                        placeholder = "(11) 90000-0000",
                        icon = "♧",
                        keyboardType = KeyboardType.Phone
                    )
                }

                1 -> {
                    CadastroField(
                        label = "CEP",
                        value = cep,
                        onValueChange = { cadastroScreenViewModel.onCepChanged(it) },
                        placeholder = "00000-000",
                        icon = "⌖",
                        keyboardType = KeyboardType.Number
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(0.8f)) {
                            CadastroField(
                                label = "Estado",
                                value = estado,
                                onValueChange = { cadastroScreenViewModel.onEstadoChanged(it) },
                                placeholder = "UF"
                            )
                        }

                        Box(Modifier.weight(1.6f)) {
                            CadastroField(
                                label = "Cidade",
                                value = cidade,
                                onValueChange = { cadastroScreenViewModel.onCidadeChanged(it) },
                                placeholder = "Sua cidade",
                                icon = "▤"
                            )
                        }
                    }

                    CadastroField(
                        label = "Endereço",
                        value = endereco,
                        onValueChange = { cadastroScreenViewModel.onEnderecoChanged(it) },
                        placeholder = "Rua, Avenida, etc.",
                        icon = "⌂"
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
                        icon = "✉",
                        keyboardType = KeyboardType.Email
                    )

                    CadastroField(
                        label = "Senha",
                        value = senha,
                        onValueChange = { cadastroScreenViewModel.onSenhaChanged(it) },
                        placeholder = "Digite sua senha",
                        icon = "♙",
                        isPassword = !mostrarSenha,
                        trailingText = if (mostrarSenha) "Ocultar" else "Ver",
                        onTrailingClick = {
                            cadastroScreenViewModel.toggleMostrarSenha()
                        },
                        keyboardType = KeyboardType.Password
                    )

                    Text(
                        text = "Mínimo de 8 caracteres",
                        fontSize = 12.sp,
                        color = CadastroGray
                    )

                    CadastroField(
                        label = "Confirmar senha",
                        value = confirmarSenha,
                        onValueChange = { cadastroScreenViewModel.onConfirmarSenhaChanged(it) },
                        placeholder = "Confirme sua senha",
                        icon = "♙",
                        isPassword = !mostrarConfirmacao,
                        trailingText = if (mostrarConfirmacao) "Ocultar" else "Ver",
                        onTrailingClick = {
                            cadastroScreenViewModel.toggleMostrarConfirmacao()
                        },
                        keyboardType = KeyboardType.Password
                    )

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
                            color = CadastroGray
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Rodapé fixo
        Button(
            onClick = {
                if (currentStep < LAST_STEP) {
                    cadastroScreenViewModel.avancarEtapa()
                } else {
                    // Cadastro real será conectado à API posteriormente.
                }

                focusManager.clearFocus()
                keyboardController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = CadastroPink
            )
        ) {
            Text(
                text = "Continuar",
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
                color = CadastroGray
            )

            Text(
                text = "Entrar",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CadastroPink,
                modifier = Modifier.clickable {
                    navController.navigate("login") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
private fun CadastroField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    trailingText: String? = null,
    onTrailingClick: (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = CadastroText,
            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(Color.White)
                .border(
                    width = 1.5.dp,
                    color = CadastroBorder,
                    shape = RoundedCornerShape(17.dp)
                )
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Text(
                    text = icon,
                    fontSize = 20.sp,
                    color = CadastroGray
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
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = CadastroText
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType
                    ),
                    visualTransformation = if (isPassword) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (trailingText != null && onTrailingClick != null) {
                Spacer(Modifier.width(6.dp))

                Text(
                    text = trailingText,
                    color = CadastroGray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        onTrailingClick()
                    }
                )
            }
        }
    }
}
