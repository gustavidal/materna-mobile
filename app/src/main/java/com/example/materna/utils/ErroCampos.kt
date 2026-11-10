package com.example.materna.utils

object ErroCampos {
    fun nome(value: String): String? {
        val nome = value.trim()

        return when {
            nome.isEmpty() ->
                "Informe seu nome completo."

            nome.length > 100 ->
                "O nome deve ter no máximo 100 caracteres."

            else -> null
        }
    }

    fun dataNascimento(value: String): String? {
        return when {
            value.isEmpty() ->
                "Informe sua data de nascimento."

            value.length < 10 ->
                "Complete a data de nascimento."

            !ValidadorCampos.dataNascimentoValida(value) ->
                "Informe uma data válida."

            else -> null
        }
    }

    fun cpf(value: String): String? {
        val numeros = value.filter { it.isDigit() }

        return when {
            numeros.isEmpty() ->
                "Informe seu CPF."

            numeros.length < 11 ->
                "Complete os 11 dígitos do CPF."

            !ValidadorCampos.cpfValido(value) ->
                "CPF inválido. Confira os números informados."

            else -> null
        }
    }

    fun telefone(value: String): String? {
        val numeros = value.filter { it.isDigit() }

        return when {
            numeros.isEmpty() ->
                "Informe seu telefone."

            !ValidadorCampos.telefoneValido(value) ->
                "Informe um telefone com DDD e 8 ou 9 dígitos."

            else -> null
        }
    }

    fun cep(value: String): String? {
        val numeros = value.filter { it.isDigit() }

        return when {
            numeros.isEmpty() ->
                "Informe seu CEP."

            !ValidadorCampos.cepValido(value) ->
                "Complete os 8 dígitos do CEP."

            else -> null
        }
    }

    fun numero(value: String): String? {
        val numero = value.toIntOrNull()

        return when {
            value.isBlank() ->
                "Informe o número do endereço."

            !ValidadorCampos.numeroValido(numero) ->
                "Informe um número maior que zero."

            else -> null
        }
    }

    fun email(value: String): String? {
        return when {
            value.isBlank() ->
                "Informe seu e-mail."

            !ValidadorCampos.emailValido(value) ->
                "E-mail inválido. Verifique o endereço informado."

            else -> null
        }
    }

    fun confirmarSenha(
        senha: String,
        confirmacao: String
    ): String? {
        return when {
            confirmacao.isBlank() ->
                "Confirme sua senha."

            !ValidadorCampos.senhasCoincidem(senha, confirmacao) ->
                "As senhas não coincidem."

            else -> null
        }
    }
}
