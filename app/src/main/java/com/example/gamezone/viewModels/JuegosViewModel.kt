package com.example.gamezone.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.gamezone.data.Game
import com.example.gamezone.data.GameRepository
import com.example.gamezone.data.GameDataInitializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class JuegosViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameDataInitializer: GameDataInitializer
) : ViewModel() {

    private val _items = MutableStateFlow<List<Game>>(emptyList())
    val items: StateFlow<List<Game>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                gameDataInitializer.initializeGameData()
            } finally {
                _isLoading.value = false
            }
        }

        gameRepository.getAllGames()
            .onEach { games -> _items.value = games }
            .launchIn(viewModelScope)
    }

}