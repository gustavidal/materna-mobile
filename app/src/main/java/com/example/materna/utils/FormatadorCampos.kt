package com.example.materna.utils

object FormatadorCampos {
    fun data(value: String): String {
        val numeros = value.filter { it.isDigit() }.take(8)

        return buildString {
            numeros.forEachIndexed { index, char ->
                if (index == 2 || index == 4) append('/')
                append(char)
            }
        }
    }

    fun cpf(value: String): String {
        val numeros = value.filter { it.isDigit() }.take(11)

        return buildString {
            numeros.forEachIndexed { index, char ->
                if (index == 3 || index == 6) append('.')
                if (index == 9) append('-')
                append(char)
            }
        }
    }

    fun telefone(value: String): String {
        val numeros = value.filter { it.isDigit() }.take(11)

        return buildString {
            numeros.forEachIndexed { index, char ->
                when {
                    index == 0 -> append('(')
                    index == 2 -> append(") ")
                    index == 6 && numeros.length <= 10 -> append('-')
                    index == 7 && numeros.length == 11 -> append('-')
                }

                append(char)
            }
        }
    }

    fun cep(value: String): String {
        val numeros = value.filter { it.isDigit() }.take(8)

        return buildString {
            numeros.forEachIndexed { index, char ->
                if (index == 5) append('-')
                append(char)
            }
        }
    }

    fun dataParaExibicao(value: String): String {
        val partes = value.take(10).split("-")

        if (partes.size != 3 ||
            partes[0].length != 4 ||
            partes[1].length != 2 ||
            partes[2].length != 2
        ) {
            return value
        }

        return "${partes[2]}/${partes[1]}/${partes[0]}"
    }

    fun dataParaApi(value: String): String? {
        val partes = value.split("/")

        if (partes.size != 3 ||
            partes[0].length != 2 ||
            partes[1].length != 2 ||
            partes[2].length != 4
        ) {
            return null
        }

        return "${partes[2]}-${partes[1]}-${partes[0]}"
    }

    fun mascararEmail(email: String): String {
        val partes = email.split("@")

        if (partes.size != 2 || partes[0].isBlank()) {
            return "E-mail não disponível"
        }

        return "${partes[0].first()}***@${partes[1]}"
    }
}
