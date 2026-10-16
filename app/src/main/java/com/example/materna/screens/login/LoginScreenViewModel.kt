package com.example.materna.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginScreenViewModel: ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    fun onEmailChanged( novoValor: String ) {
        _email.value = novoValor
    }



    private val _senha = MutableLiveData<String>()
    val senha: LiveData<String> = _senha

    fun onSenhaChanged( novoValor: String ) {
        _senha.value = novoValor
    }
}