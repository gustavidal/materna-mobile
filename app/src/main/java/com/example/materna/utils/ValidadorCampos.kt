package com.example.materna.utils

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ValidadorCampos {
    fun nomeValido(value: String): Boolean {
        val nome = value.trim()

        return nome.isNotEmpty() && nome.length <= 100
    }

    fun dataNascimentoValida(value: String): Boolean {
        if (!value.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
            return false
        }

        val formato =
            SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault(),
                )
                .apply {
                    isLenient = false
                }

        val data =
            try {
                formato.parse(value)
            } catch (e: Exception) {
                null
            } ?: return false

        val hoje = Calendar.getInstance()

        val nascimento =
            Calendar.getInstance().apply {
                time = data
            }

        return !nascimento.after(hoje)
    }

    fun cpfValido(value: String): Boolean {
        val numeros = value.filter { it.isDigit() }

        if (numeros.length != 11) return false
        if (numeros.all { it == numeros[0] }) return false

        val digitos = numeros.map { it.digitToInt() }

        var soma = 0

        for (i in 0 until 9) {
            soma += digitos[i] * (10 - i)
        }

        val restoPrimeiro = (soma * 10) % 11
        val primeiroDigito = if (restoPrimeiro == 10) 0 else restoPrimeiro

        if (digitos[9] != primeiroDigito) return false

        soma = 0

        for (i in 0 until 10) {
            soma += digitos[i] * (11 - i)
        }

        val restoSegundo = (soma * 10) % 11
        val segundoDigito = if (restoSegundo == 10) 0 else restoSegundo

        return digitos[10] == segundoDigito
    }

    fun telefoneValido(value: String): Boolean {
        val numeros = value.filter { it.isDigit() }
        return numeros.length == 10 || numeros.length == 11
    }

    fun cepValido(value: String): Boolean {
        val numeros = value.filter { it.isDigit() }
        return numeros.length == 8
    }

    fun numeroValido(value: Int?): Boolean {
        return value != null && value > 0
    }

    fun emailValido(value: String): Boolean {
        val usuario = value.substringBefore("@")

        return Patterns.EMAIL_ADDRESS.matcher(value).matches() &&
            usuario.length in 6..256 &&
            !usuario.all { it.isDigit() }
    }

    fun senhaTemOitoCaracteres(value: String): Boolean {
        return value.length >= 8
    }

    fun senhaTemMaiuscula(value: String): Boolean {
        return value.any { it.isUpperCase() }
    }

    fun senhaTemNumero(value: String): Boolean {
        return value.any { it.isDigit() }
    }

    fun senhaTemCaractereEspecial(value: String): Boolean {
        return value.any {
            !it.isLetterOrDigit() && !it.isWhitespace()
        }
    }

    fun senhaValida(value: String): Boolean {
        return senhaTemOitoCaracteres(value) &&
            senhaTemMaiuscula(value) &&
            senhaTemNumero(value) &&
            senhaTemCaractereEspecial(value)
    }

    fun senhasCoincidem(
        senha: String,
        confirmacao: String,
    ): Boolean {
        return confirmacao.isNotEmpty() && senha == confirmacao
    }
}
