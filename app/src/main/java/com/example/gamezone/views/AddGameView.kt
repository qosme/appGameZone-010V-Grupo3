package com.example.gamezone.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gamezone.R
import com.example.gamezone.data.Game
import com.example.gamezone.viewModels.AdminProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameView(
    onBack: () -> Unit = {},
    onGameAdded: () -> Unit = {},
    vm: AdminProfileViewModel = hiltViewModel()
) {
    var gameName by remember { mutableStateOf("") }
    var gameDescription by remember { mutableStateOf("") }
    var longDescription by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var releaseDate by remember { mutableStateOf("") }
    var developer by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(true) }

    val isLoading = vm.isLoading.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Agregar Juego") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Agregar Nuevo Juego",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Game Name
                    OutlinedTextField(
                        value = gameName,
                        onValueChange = { gameName = it },
                        label = { Text("Nombre del Juego *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Description
                    OutlinedTextField(
                        value = gameDescription,
                        onValueChange = { gameDescription = it },
                        label = { Text("Descripción Corta *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Long Description
                    OutlinedTextField(
                        value = longDescription,
                        onValueChange = { longDescription = it },
                        label = { Text("Descripción Larga *") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Price
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Rating
                    OutlinedTextField(
                        value = rating,
                        onValueChange = { rating = it },
                        label = { Text("Calificación (0-5) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Release Date
                    OutlinedTextField(
                        value = releaseDate,
                        onValueChange = { releaseDate = it },
                        label = { Text("Fecha de Lanzamiento (YYYY-MM-DD) *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Developer
                    OutlinedTextField(
                        value = developer,
                        onValueChange = { developer = it },
                        label = { Text("Desarrollador *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Publisher
                    OutlinedTextField(
                        value = publisher,
                        onValueChange = { publisher = it },
                        label = { Text("Editor *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Availability
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = isAvailable,
                            onCheckedChange = { isAvailable = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Disponible para venta")
                    }
                }
            }

            // Add Game Button
            Button(
                onClick = {
                    if (validateGameData(gameName, gameDescription, longDescription, price, category, rating, releaseDate, developer, publisher)) {
                        val newGame = Game(
                            id = "game_${System.currentTimeMillis()}",
                            name = gameName,
                            description = gameDescription,
                            longDescription = longDescription,
                            price = price.toDoubleOrNull() ?: 0.0,
                            category = category,
                            rating = rating.toDoubleOrNull() ?: 0.0,
                            releaseDate = releaseDate,
                            developer = developer,
                            publisher = publisher,
                            imageResId = R.drawable.gamezonelogo, // Default image
                            isAvailable = isAvailable
                        )
                        
                        scope.launch {
                            vm.addGame(newGame)
                            snackbarHostState.showSnackbar("Juego agregado exitosamente")
                            onGameAdded()
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Por favor completa todos los campos requeridos")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Agregando..." else "Agregar Juego")
            }
        }
    }
}

private fun validateGameData(
    name: String,
    description: String,
    longDescription: String,
    price: String,
    category: String,
    rating: String,
    releaseDate: String,
    developer: String,
    publisher: String
): Boolean {
    return name.isNotBlank() &&
            description.isNotBlank() &&
            longDescription.isNotBlank() &&
            price.isNotBlank() && price.toDoubleOrNull() != null &&
            category.isNotBlank() &&
            rating.isNotBlank() && rating.toDoubleOrNull() != null &&
            releaseDate.isNotBlank() &&
            developer.isNotBlank() &&
            publisher.isNotBlank()
}
