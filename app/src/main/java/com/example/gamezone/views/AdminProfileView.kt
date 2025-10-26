package com.example.gamezone.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gamezone.data.Game
import com.example.gamezone.viewModels.AdminProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileView(
    userEmail: String,
    onNavigateToAddGame: () -> Unit = {},
    vm: AdminProfileViewModel = hiltViewModel()
) {
    // Collecting game state from ViewModel
    val games by vm.games.collectAsState(initial = emptyList()) // Delegate for state
    val isLoading by vm.isLoading.collectAsState(initial = false) // Delegate for loading state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Log the game list size to see if it updates correctly
    LaunchedEffect(games) {
        Log.d("AdminProfileView", "Games list updated: ${games.size} items")
    }

    // Only load games if the list is empty
    LaunchedEffect(Unit) {
        if (games.isEmpty()) {
            Log.d("AdminProfileView", "Loading games as the list is empty.")
            vm.loadGames()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddGame) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Juego")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Admin Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Panel de Administración",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gestiona los juegos disponibles en la tienda",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Games Management Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Juegos Disponibles (${games.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToAddGame) {
                            Text("Agregar Nuevo")
                        }
                    }

                    // Show loading spinner if data is loading
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (games.isEmpty()) {
                        // Show message if no games available
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay juegos disponibles",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        // Show games list
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(games, key = { it.id }) { game ->
                                GameManagementCard(
                                    game = game,
                                    onEdit = { /* TODO: Navigate to edit game */ },
                                    onDelete = { gameId ->
                                        scope.launch {
                                            vm.deleteGame(gameId)
                                            snackbarHostState.showSnackbar("Juego eliminado")
                                        }
                                    },
                                    onToggleAvailability = { gameId, isAvailable ->
                                        scope.launch {
                                            vm.toggleGameAvailability(gameId, isAvailable)
                                            snackbarHostState.showSnackbar(
                                                if (isAvailable) "Juego habilitado" else "Juego deshabilitado"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}






@Composable
private fun GameManagementCard(
    game: Game,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit,
    onToggleAvailability: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = game.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = game.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", game.price)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar"
                        )
                    }
                    IconButton(
                        onClick = { onDelete(game.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Text(
                text = game.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Desarrollador: ${game.developer}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Switch(
                    checked = game.isAvailable,
                    onCheckedChange = { onToggleAvailability(game.id, it) }
                )
            }
        }
    }
}
