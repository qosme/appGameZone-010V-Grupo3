package com.example.gamezone.viewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BienvenidaViewModel @Inject constructor() : ViewModel() {
    private val _texto = MutableStateFlow("Bienvenido a Gamezone")
    val texto: StateFlow<String> = _texto
}