package com.example.gamezone.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.gamezone.navigation.Route
import com.example.gamezone.viewModels.JuegosViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuegosView(
    navController: NavController,
    vm: JuegosViewModel = hiltViewModel()
) {
    val items = vm.items.collectAsState().value
    val isLoading = vm.isLoading.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Juegos",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Encuentra los juegos disponibles en la aplicación.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(items) { index, item ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Abriendo detalle de ${item.name}")
                                    }
                                    navController.navigate(Route.JuegosDetalle.build(id = item.id)) {
                                        launchSingleTop = true
                                        // Ejemplo de control de back stack:
                                        popUpTo(Route.Juegos.route) { inclusive = false }
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Display the image
                                Image(
                                    painter = painterResource(id = item.imageResId),
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .size(64.dp) // Adjust as needed
                                        .padding(end = 8.dp)
                                )

                                // Game info
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "$${String.format("%.0f", item.price)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = item.category,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                // "Ver detalle" label
                                Text("Ver detalle", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun JuegosDetalleView(
    id: String,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit = {}
) {
    // This will be replaced with the enhanced version
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Detalle", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("ID recibido en la ruta: $id", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onBack) { Text("Volver") }
    }
}