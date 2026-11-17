package com.example.materna.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionEvents {
    private val _sessaoExpirada = MutableStateFlow(false)
    val sessaoExpirada = _sessaoExpirada.asStateFlow()

    fun notificarSessaoExpirada() {
        _sessaoExpirada.value = true
    }

    fun consumirEvento() {
        _sessaoExpirada.value = false
    }
}
