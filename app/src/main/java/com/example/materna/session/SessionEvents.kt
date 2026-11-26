package com.example.materna.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionEvents {
    private val _sessaoExpirada = MutableStateFlow(false)
    val sessaoExpirada = _sessaoExpirada.asStateFlow()

    private val _logoutRealizado = MutableStateFlow(false)
    val logoutRealizado = _logoutRealizado.asStateFlow()

    fun notificarSessaoExpirada() {
        _sessaoExpirada.value = true
    }

    fun notificarLogout() {
        _logoutRealizado.value = true
    }

    fun consumirEvento() {
        _sessaoExpirada.value = false
        _logoutRealizado.value = false
    }
}