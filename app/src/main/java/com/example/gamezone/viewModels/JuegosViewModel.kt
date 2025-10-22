package com.example.gamezone.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.gamezone.data.Producto
import com.example.gamezone.R

class JuegosViewModel : ViewModel() {
    // Simulamos una lista simple para navegar a detalle
    private val productList = listOf(
        Producto("Grand Theft Auto V", R.drawable.gtav),
        Producto("The Sims 4", R.drawable.sims4),
        Producto("Terraria", R.drawable.terraria),
        Producto("Portal 2", R.drawable.portal),
        Producto("Minecraft", R.drawable.minecraft),
        Producto("Europa Universalis IV", R.drawable.europauniv),
        Producto("F1 25", R.drawable.f125),
        Producto("Farming Simulator 25", R.drawable.farming)
    )

    private val _items = MutableStateFlow(productList)
    val items: StateFlow<List<Producto>> = _items



}