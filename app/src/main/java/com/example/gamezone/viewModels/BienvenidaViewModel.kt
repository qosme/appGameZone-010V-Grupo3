package com.example.gamezone.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BienvenidaViewModel : ViewModel() {
    private val _texto = MutableStateFlow("Bienvenido a Gamezone")
    val texto: StateFlow<String> = _texto
}