package com.example.gamezone.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gamezone.data.Game
import com.example.gamezone.viewModels.CartViewModel
import com.example.gamezone.viewModels.GameDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailView(
    gameId: String,
    userEmail: String,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    vm: GameDetailViewModel = hiltViewModel(),
    cartVM: CartViewModel = hiltViewModel()
) {
    val game = vm.game.collectAsState().value
    val isLoading = vm.isLoading.collectAsState().value
    val addToCartResult by cartVM.addToCartResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()



    // Load game details
    LaunchedEffect(userEmail) {
        cartVM.setUser(userEmail)
        vm.loadGameDetails(gameId)
    }



    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Juego") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("← Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (game != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Game Image
                Image(
                    painter = painterResource(id = game.imageResId),
                    contentDescription = game.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Game Title and Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = game.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${String.format("%.0f", game.price)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${game.rating}/5.0",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Category and Release Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Categoría",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = game.category,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Fecha de Lanzamiento",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = game.releaseDate,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Developer and Publisher
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Información del Juego",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Desarrollador",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = game.developer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Column {
                                Text(
                                    text = "Editor",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = game.publisher,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Description
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = game.longDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                // Add to Cart Button
                Button(
                    onClick = {
                        game?.let {
                            cartVM.addToCart(it.id, it.price)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = game?.isAvailable == true
                ) {
                    Text(if (game?.isAvailable == true) "Agregar al Carrito" else "No Disponible")
                }







                // View Cart Button
                OutlinedButton(
                    onClick = onNavigateToCart,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver Carrito")
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Juego no encontrado")
            }
        }
    }

    // Handle add to cart result
    LaunchedEffect(addToCartResult) {
        addToCartResult?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                cartVM.clearAddToCartResult() // reset after showing
            }
        }
    }
}
