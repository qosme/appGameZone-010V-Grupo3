package com.example.gamezone.views


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gamezone.viewModels.AdminProfileViewModel
import com.example.gamezone.viewModels.GameDetailViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGameView(
    gameId: String,
    onSave: () -> Unit, // Navigate back after save
    onCancel: () -> Unit, // Navigate back on cancel
    vm: AdminProfileViewModel = hiltViewModel(),
    gameDetailVM: GameDetailViewModel = hiltViewModel()
) {
    // Fetch game details from GameDetailViewModel
    val gameState = gameDetailVM.game.collectAsState().value
    val isLoadingState = gameDetailVM.isLoading.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Load game details when gameId changes
    LaunchedEffect(gameId) {
        gameDetailVM.loadGameDetails(gameId)
    }

    // If game is null, show a loading state or an error message
    if (gameState == null) {
        if (isLoadingState) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Juego no encontrado o error al cargar")
            }
        }
        return
    }

    // Editable states for the game fields
    val nameState = remember { mutableStateOf(gameState.name) }
    val categoryState = remember { mutableStateOf(gameState.category) }
    val priceState = remember { mutableStateOf(gameState.price.toString()) }
    val descriptionState = remember { mutableStateOf(gameState.description) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar Juego",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Show loading indicator when data is being fetched
            if (isLoadingState) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Edit Game Form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Game name field
                        TextField(
                            value = nameState.value,
                            onValueChange = { nameState.value = it },
                            label = { Text("Nombre del juego") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Category field
                        TextField(
                            value = categoryState.value,
                            onValueChange = { categoryState.value = it },
                            label = { Text("Categoría") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Price field
                        TextField(
                            value = priceState.value,
                            onValueChange = { priceState.value = it },
                            label = { Text("Precio") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Description field
                        TextField(
                            value = descriptionState.value,
                            onValueChange = { descriptionState.value = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Cancel Button
                            Button(onClick = onCancel) {
                                Text("Cancelar")
                            }

                            // Save Button
                            Button(
                                onClick = {
                                    // Update game with the modified data
                                    val updatedGame = gameState.copy(
                                        name = nameState.value,
                                        category = categoryState.value,
                                        price = priceState.value.toDoubleOrNull() ?: gameState.price,
                                        description = descriptionState.value
                                    )
                                    // Save the updated game
                                    vm.updateGame(updatedGame)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Juego actualizado exitosamente")
                                    }
                                    onSave() // Navigate back to the previous screen
                                }
                            ) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}
