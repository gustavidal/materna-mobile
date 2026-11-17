package com.example.materna.screens.cadastro

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.materna.R
import com.example.materna.components.CadastroField
import com.example.materna.components.CadastroStepIndicator
import com.example.materna.components.SenhaConfirmacao
import com.example.materna.components.SenhaRequisitos
import kotlinx.coroutines.delay

private const val LAST_STEP = 2

@Composable
fun CadastroScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    cadastroScreenViewModel: CadastroScreenViewModel
) {
    val keyboardController =  LocalSoftwareKeyboardController.current
    val focusManager       =  LocalFocusManager.current

    val currentStep        by cadastroScreenViewModel.currentStep.observeAsState(0)

    val camposVisitados    by cadastroScreenViewModel.camposVisitados.observeAsState(emptySet())

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
    val complemento        by cadastroScreenViewModel.complemento.observeAsState(null)

    val email              by cadastroScreenViewModel.email.observeAsState("")
    val senha              by cadastroScreenViewModel.senha.observeAsState("")
    val confirmarSenha     by cadastroScreenViewModel.confirmarSenha.observeAsState("")
    val termosAceitos      by cadastroScreenViewModel.termosAceitos.observeAsState(false)

    val mostrarSenha       by cadastroScreenViewModel.mostrarSenha.observeAsState(false)
    val mostrarConfirmacao by cadastroScreenViewModel.mostrarConfirmacao.observeAsState(false)

    val cadastrando        by cadastroScreenViewModel.cadastrando.observeAsState(false)
    val mensagemCadastro   by cadastroScreenViewModel.mensagemCadastro.observeAsState(null)

    val consultandoCep     by cadastroScreenViewModel.consultandoCep.observeAsState(false)
    val erroCep            by cadastroScreenViewModel.erroCep.observeAsState(null)

    val cadastroConcluido  by cadastroScreenViewModel.cadastroConcluido.observeAsState(false)
    val idDoadora          by cadastroScreenViewModel.idDoadora.observeAsState(null)

    val context = LocalContext.current

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

    LaunchedEffect(cadastroConcluido, idDoadora) {
        if (cadastroConcluido && idDoadora != null) {
            navController.navigate("confirmar-email/${idDoadora}") {
                popUpTo("cadastro") {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    // Layout principal: o rodapé fica fora da área rolável.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream))
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Área rolável: cabeçalho, indicadores, campos e mensagens.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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
            // Cabeçalho
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        tint = colorResource(R.color.main_text_color),
                        contentDescription = "Seta para esquerda (voltar)"
                    )
                }

                Text(
                    text = "Etapa ${currentStep + 1} de 3",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colorResource(R.color.background_gray))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 13.sp,
                    color = colorResource(R.color.secundary_gray)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Indicadores das etapas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CadastroStepIndicator(
                    currentStep = currentStep,
                    totalSteps = LAST_STEP + 1
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

            // Campos das três etapas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (currentStep) {
                    // ETAPA 1: dados pessoais
                    0 -> {
                        CadastroField(
                            label = "Nome completo",
                            value = nome,
                            onValueChange = {
                                cadastroScreenViewModel.onNomeChanged(it)
                            },
                            placeholder = "Maria Silva",
                            icon = painterResource(R.drawable.ic_profile),
                            capitalization = KeyboardCapitalization.Words,
                            maxLength = 100,
                            onFocusLost = {
                                cadastroScreenViewModel.onCampoPerdeuFoco(CadastroCampos.NOME)
                            }
                        )
                        if (CadastroCampos.NOME in camposVisitados) {
                            cadastroScreenViewModel.erroNome()?.let { erro ->
                                Text(
                                    text = erro,
                                    fontSize = 12.sp,
                                    color = colorResource(R.color.red)
                                )
                            }
                        }

                        CadastroField(
                            label = "Data de nascimento",
                            value = dataNascimento,
                            onValueChange = {
                                cadastroScreenViewModel.onDataNascimentoChanged(it)
                            },
                            placeholder = "dd/mm/aaaa",
                            icon = painterResource(R.drawable.ic_calendar),
                            keyboardType = KeyboardType.Number,
                            onFocusLost = {
                                cadastroScreenViewModel.onCampoPerdeuFoco(CadastroCampos.DATA_NASCIMENTO)
                            }
                        )
                        if (CadastroCampos.DATA_NASCIMENTO in camposVisitados) {
                            cadastroScreenViewModel.erroDataNascimento()?.let { erro ->
                                Text(
                                    text = erro,
                                    fontSize = 12.sp,
                                    color = colorResource(R.color.red)
                                )
                            }
                        }

                        CadastroField(
                            label = "CPF",
                            value = cpf,
                            onValueChange = {
                                cadastroScreenViewModel.onCpfChanged(it)
                            },
                            placeholder = "000.000.000-00",
                            icon = painterResource(R.drawable.ic_document),
                            keyboardType = KeyboardType.Number,
                            onFocusLost = {
                                cadastroScreenViewModel.onCampoPerdeuFoco(CadastroCampos.CPF)
                            }
                        )
                        if (CadastroCampos.CPF in camposVisitados) {
                            cadastroScreenViewModel.erroCpf()?.let { erro ->
                                Text(
                                    text = erro,
                                    fontSize = 12.sp,
                                    color = colorResource(R.color.red)
                                )
                            }
                        }

                        CadastroField(
                            label = "Telefone",
                            value = telefone,
                            onValueChange = {
                                cadastroScreenViewModel.onTelefoneChanged(it)
                            },
                            placeholder = "(11) 90000-0000",
                            icon = painterResource(R.drawable.ic_phone),
                            keyboardType = KeyboardType.Phone,
                            onFocusLost = {
                                cadastroScreenViewModel.onCampoPerdeuFoco(CadastroCampos.TELEFONE)
                            }
                        )
                        if (CadastroCampos.TELEFONE in camposVisitados) {
                            cadastroScreenViewModel.erroTelefone()?.let { erro ->
                                Text(
                                    text = erro,
                                    fontSize = 12.sp,
                                    color = colorResource(R.color.red)
                                )
                            }
                        }
                    }

                    // ETAPA 2: endereço
                    1 -> {
                        CadastroField(
                            label = "CEP",
                            value = cep,
                            onValueChange = {
                                cadastroScreenViewModel.onCepChanged(it)
                            },
                            placeholder = "00000-000",
                            icon = painterResource(R.drawable.ic_pin_location),
                            keyboardType = KeyboardType.Number,
                            onFocusLost = {
                                cadastroScreenViewModel.onCampoPerdeuFoco(CadastroCampos.CEP)
                            }
                        )
                        if (consultandoCep) {
                            Text(
                                text = "Consultando CEP...",
                                fontSize = 12.sp,
                                color = colorResource(R.color.secundary_gray)
                            )
                        }
                        if (CadastroCampos.CEP in camposVisitados) {
                            cadastroScreenViewModel.erroCepPreenchimento()?.let { erro ->
                                Text(
                                    text = erro,
                                    fontSize = 12.sp,
                                    color = colorResource(R.color.red)
                                )
                            }
                        }
                        if (erroCep != null) {
                            Text(
                                text = erroCep.orEmpty(),
                                fontSize = 12.sp,
                                color = colorResource(R.color.red)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(Modifier.weight(0.8f)) {
                                CadastroField(
                                    label = "Estado",
                                    value = estado,
                                    onValueChange = {
                                        cadastroScreenViewModel.onEstadoChanged(it)
                                    },
                                    placeholder = "UF",
                                    enabled = false
                                )
                            }

                            Box(Modifier.weight(1.6f)) {
                                CadastroField(
                                    label = "Cidade",
                                    value = cidade,
                                    onValueChange = {
                                        cadastroScreenViewModel.onCidadeChanged(it)
                                    },
                                    placeholder = "Sua cidade",
                                    icon = painterResource(R.drawable.ic_building),
                                    enabled = false
                                )
                            }
                        }

                        CadastroField(
                            label = "Logradouro",
                            value = logradouro,
                            onValueChange = {
                                cadastroScreenViewModel.onLogradouroChanged(it)
                            },
                            placeholder = "Rua, Avenida, etc.",
                            icon = painterResource(R.drawable.ic_house),
                            enabled = false
                        )

                        CadastroField(
                            label = "Bairro",
                            value = bairro,
                            onValueChange = {
                                cadastroScreenViewModel.onBairroChanged(it)
                            },
                            placeholder = "Seu bairro",
                            icon = painterResource(R.drawable.ic_neighborhood),
                            enabled = false
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier.weight(0.8f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CadastroField(
                                    label = "Número",
                                    value = numero?.toString().orEmpty(),
                                    onValueChange = {
                                        cadastroScreenViewModel.onNumeroChanged(
                                            it.toIntOrNull()
                                        )
                                    },
                                    placeholder = "123",
                                    keyboardType = KeyboardType.Number,
                                    onFocusLost = {
                                        cadastroScreenViewModel.onCampoPerdeuFoco(CadastroCampos.NUMERO)
                                    }
                                )
                                if (CadastroCampos.NUMERO in camposVisitados) {
                                    cadastroScreenViewModel.erroNumero()?.let { erro ->
                                        Text(
                                            text = erro,
                                            fontSize = 12.sp,
                                            color = colorResource(R.color.red)
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier.weight(1.6f)
                            ) {
                                CadastroField(
                                    label = "Complemento",
                                    value = complemento.orEmpty(),
                                    onValueChange = {
                                        cadastroScreenViewModel.onComplementoChanged(it)
                                    },
                                    placeholder = "Apto, Bloco, etc.",
                                    capitalization = KeyboardCapitalization.Words
                                )
                            }
                        }
                    }

                    // ETAPA 3: acesso e senha
                    2 -> {
                        CadastroField(
                            label = "E-mail",
                            value = email,
                            onValueChange = {
                                cadastroScreenViewModel.onEmailChanged(it)
                            },
                            placeholder = "seu@email.com",
                            icon = painterResource(R.drawable.ic_mail),
                            keyboardType = KeyboardType.Email,
                            onFocusLost = {
                                cadastroScreenViewModel.onCampoPerdeuFoco(CadastroCampos.EMAIL)
                            }
                        )
                        if (CadastroCampos.EMAIL in camposVisitados) {
                            cadastroScreenViewModel.erroEmail()?.let { erro ->
                                Text(
                                    text = erro,
                                    fontSize = 12.sp,
                                    color = colorResource(R.color.red)
                                )
                            }
                        }

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
                        SenhaRequisitos(senha = senha)

                        CadastroField(
                            label = "Confirmar senha",
                            value = confirmarSenha,
                            onValueChange = {
                                cadastroScreenViewModel.onConfirmarSenhaChanged(it)
                            },
                            placeholder = "Confirme sua senha",
                            icon = painterResource(R.drawable.ic_lock),
                            isPassword = !mostrarConfirmacao,
                            trailingIcon = if (mostrarConfirmacao) painterResource(R.drawable.ic_visibility_off) else painterResource(
                                R.drawable.ic_visibility_on
                            ),
                            onTrailingClick = {
                                cadastroScreenViewModel.toggleMostrarConfirmacao()
                            },
                            keyboardType = KeyboardType.Password,
                            onFocusLost = {
                                cadastroScreenViewModel.onCampoPerdeuFoco(CadastroCampos.CONFIRMAR_SENHA)
                            }
                        )
                        SenhaConfirmacao(
                            senha = senha,
                            confirmacao = confirmarSenha,
                            campoVisitado = CadastroCampos.CONFIRMAR_SENHA in camposVisitados
                        )



                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = termosAceitos,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colorResource(R.color.main_pink),
                                    uncheckedColor = colorResource(R.color.background_gray)
                                ),
                                onCheckedChange = {
                                    cadastroScreenViewModel.onTermosAceitosChanged(it)
                                }
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
                    color = if (cadastroConcluido) {
                        colorResource(R.color.green)
                    } else {
                        colorResource(R.color.red)
                    },
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Rodapé fixo: botão principal
        Button(
            onClick = {
                if (currentStep < LAST_STEP) {
                    cadastroScreenViewModel.avancarEtapa()
                } else {
                    cadastroScreenViewModel.cadastrarDoadora(context)
                }

                focusManager.clearFocus()
                keyboardController?.hide()
            },
            enabled = cadastroScreenViewModel.podeContinuar() && !cadastrando,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.main_pink),
                disabledContainerColor = colorResource(R.color.border)
            )
        ) {
            Text(
                text = when {
                    cadastrando -> "Cadastrando..."
                    currentStep == LAST_STEP -> "Finalizar cadastro"
                    else -> "Continuar"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(14.dp))

        // Rodapé fixo: link para entrar
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
                modifier = Modifier
                    .clickable {
                        navController.navigate("login") {
                            popUpTo("cadastro") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
            )
        }
    }
}