package com.example.materna.screens.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardScreenViewModel : ViewModel() {

    private val _nomeDoadora = MutableLiveData<String>()
    val nomeDoadora: LiveData<String> = _nomeDoadora

    private val _bancoSelecionado = MutableLiveData<Boolean>()
    val bancoSelecionado: LiveData<Boolean> = _bancoSelecionado

    fun atualizarNome(nome: String) {
        _nomeDoadora.value = nome
    }

    fun atualizarBancoSelecionado(selecionado: Boolean) {
        _bancoSelecionado.value = selecionado
    }
}
