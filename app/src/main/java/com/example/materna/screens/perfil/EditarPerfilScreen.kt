package com.example.materna.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun EditarPerfilScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    editarPerfilScreenViewModel: EditarPerfilScreenViewModel
) {
    val nome                 by editarPerfilScreenViewModel.nome.observeAsState("")
    val email                by editarPerfilScreenViewModel.email.observeAsState("")
    val cpf                  by editarPerfilScreenViewModel.cpf.observeAsState("")
    val cep                  by editarPerfilScreenViewModel.cep.observeAsState("")
    val estado               by editarPerfilScreenViewModel.siglaEstado.observeAsState("")
    val cidade               by editarPerfilScreenViewModel.cidade.observeAsState("")
    val bairro               by editarPerfilScreenViewModel.bairro.observeAsState("")
    val logradouro           by editarPerfilScreenViewModel.logradouro.observeAsState("")
    val numero               by editarPerfilScreenViewModel.numero.observeAsState("")
    val complemento          by editarPerfilScreenViewModel.complemento.observeAsState("")
    val dataNascimento       by editarPerfilScreenViewModel.dataNascimento.observeAsState("")
    val telefone             by editarPerfilScreenViewModel.telefone.observeAsState("")
    val senha                by editarPerfilScreenViewModel.senha.observeAsState("")
    val confirmarSenha       by editarPerfilScreenViewModel.confirmarSenha.observeAsState("")

    val consultandoCep       by editarPerfilScreenViewModel.consultandoCep.observeAsState(false)
    val erroCep              by editarPerfilScreenViewModel.erroCep.observeAsState(null)

    val carregando           by editarPerfilScreenViewModel.carregando.observeAsState(false)
    val salvando             by editarPerfilScreenViewModel.salvando.observeAsState(false)
    val mensagemErro         by editarPerfilScreenViewModel.mensagemErro.observeAsState(null)

    val atualizacaoConcluida by editarPerfilScreenViewModel.atualizacaoConcluida.observeAsState(false)

    LaunchedEffect(atualizacaoConcluida) {
        if (atualizacaoConcluida) {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("perfil_atualizado", true)

            navController.popBackStack()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_cream))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = colorResource(R.color.background_gray),
                        shape = CircleShape
                    )
                    .clickable {
                        navController.popBackStack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Voltar",
                    tint = colorResource(R.color.main_text_color)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column {
                Text(
                    text = "Editar informações",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.main_text_color)
                )

                Text(
                    text = "Atualize seus dados pessoais",
                    fontSize = 14.sp,
                    color = colorResource(R.color.secundary_gray)
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CadastroField(
                label = "Nome completo",
                value = nome,
                onValueChange = editarPerfilScreenViewModel::onNomeChanged,
                placeholder = "Nome completo",
                icon = painterResource(R.drawable.ic_profile),
                capitalization = KeyboardCapitalization.Words,
                maxLength = 100
            )

            CadastroField(
                label = "E-mail",
                value = email,
                onValueChange = editarPerfilScreenViewModel::onEmailChanged,
                placeholder = "seu@email.com",
                icon = painterResource(R.drawable.ic_mail),
                keyboardType = KeyboardType.Email
            )

            CadastroField(
                label = "CPF",
                value = cpf,
                onValueChange = editarPerfilScreenViewModel::onCpfChanged,
                placeholder = "000.000.000-00",
                icon = painterResource(R.drawable.ic_document),
                keyboardType = KeyboardType.Number
            )

            CadastroField(
                label = "CEP",
                value = cep,
                onValueChange = editarPerfilScreenViewModel::onCepChanged,
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
                    color = colorResource(R.color.red)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(Modifier.weight(0.8f)) {
                    CadastroField(
                        label = "Estado",
                        value = estado,
                        onValueChange = editarPerfilScreenViewModel::onSiglaEstadoChanged,
                        placeholder = "UF",
                        enabled = false
                    )
                }

                Box(Modifier.weight(1.6f)) {
                    CadastroField(
                        label = "Cidade",
                        value = cidade,
                        onValueChange = editarPerfilScreenViewModel::onCidadeChanged,
                        placeholder = "Sua cidade",
                        icon = painterResource(R.drawable.ic_building),
                        enabled = false
                    )
                }
            }

            CadastroField(
                label = "Bairro",
                value = bairro,
                onValueChange = editarPerfilScreenViewModel::onBairroChanged,
                placeholder = "Seu bairro",
                icon = painterResource(R.drawable.ic_neighborhood),
                enabled = false
            )

            CadastroField(
                label = "Endereço",
                value = logradouro,
                onValueChange = editarPerfilScreenViewModel::onLogradouroChanged,
                placeholder = "Rua, Avenida, etc.",
                icon = painterResource(R.drawable.ic_house),
                enabled = false
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(Modifier.weight(0.8f)) {
                    CadastroField(
                        label = "Número",
                        value = numero,
                        onValueChange = editarPerfilScreenViewModel::onNumeroChanged,
                        placeholder = "123",
                        keyboardType = KeyboardType.Number
                    )
                }

                Box(Modifier.weight(1.6f)) {
                    CadastroField(
                        label = "Complemento",
                        value = complemento,
                        onValueChange = editarPerfilScreenViewModel::onComplementoChanged,
                        placeholder = "Apto, Bloco, etc.",
                        capitalization = KeyboardCapitalization.Words
                    )
                }
            }

            CadastroField(
                label = "Data de nascimento",
                value = dataNascimento,
                onValueChange = editarPerfilScreenViewModel::onDataNascimentoChanged,
                placeholder = "dd/mm/aaaa",
                icon = painterResource(R.drawable.ic_calendar),
                keyboardType = KeyboardType.Number
            )

            CadastroField(
                label = "Telefone",
                value = telefone,
                onValueChange = editarPerfilScreenViewModel::onTelefoneChanged,
                placeholder = "(11) 90000-0000",
                icon = painterResource(R.drawable.ic_phone),
                keyboardType = KeyboardType.Phone
            )

            CadastroField(
                label = "Senha",
                value = senha,
                onValueChange = editarPerfilScreenViewModel::onSenhaChanged,
                placeholder = "Digite sua senha",
                icon = painterResource(R.drawable.ic_lock),
                isPassword = true,
                keyboardType = KeyboardType.Password
            )

            CadastroField(
                label = "Confirmar senha",
                value = confirmarSenha,
                onValueChange = editarPerfilScreenViewModel::onConfirmarSenhaChanged,
                placeholder = "Confirme sua senha",
                icon = painterResource(R.drawable.ic_lock),
                isPassword = true,
                keyboardType = KeyboardType.Password
            )


            if (mensagemErro != null) {
                Text(
                    text = mensagemErro.orEmpty(),
                    fontSize = 13.sp,
                    color = colorResource(R.color.red)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    editarPerfilScreenViewModel.atualizarDoadora()
                },
                enabled = editarPerfilScreenViewModel.podeConfirmar(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.main_pink),
                    contentColor = colorResource(R.color.main_pink),
                    disabledContainerColor = colorResource(R.color.main_pink).copy(alpha = 0.4f),
                    disabledContentColor = colorResource(R.color.disabled)
                )
            ) {
                if (salvando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = colorResource(R.color.main_pink),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Confirmar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}
