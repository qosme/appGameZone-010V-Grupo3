package com.example.gamezone.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gamezone.data.CartItemWithGame
import com.example.gamezone.viewModels.CartViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartView(
    userEmail: String,
    vm: CartViewModel = hiltViewModel(),
    onNavigateToCheckout: () -> Unit = {}
) {
    // Make sure the cart is loaded
    LaunchedEffect(userEmail) {
        vm.setUser(userEmail)
    }

    val cartItems by vm.cartItems.collectAsState()
    val totalAmount by vm.totalAmount.collectAsState()
    val itemCount by vm.itemCount.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (cartItems.isEmpty()) {
            Text("El carrito está vacío")
        } else {
            LazyColumn {
                items(cartItems) { cartItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Replace with your image loading method
                            Image(
                                painter = painterResource(id = cartItem.imageResId),
                                contentDescription = cartItem.gameName,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    cartItem.gameName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "$${String.format("%.0f", cartItem.price)} c/u",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        vm.updateQuantity(
                                            cartItem.id,
                                            cartItem.quantity - 1
                                        )
                                    }) {
                                        Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                                    }
                                    Text("${cartItem.quantity}", modifier = Modifier.padding(horizontal = 8.dp))
                                    IconButton(onClick = {
                                        vm.updateQuantity(
                                            cartItem.id,
                                            cartItem.quantity + 1
                                        )
                                    }) {
                                        Icon(Icons.Default.Add, contentDescription = "Aumentar")
                                    }
                                }

                                Text(
                                    "Subtotal: $${String.format("%.0f", cartItem.price * cartItem.quantity)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(onClick = { vm.removeFromCart(cartItem.id) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Total: $${String.format("%.0f", totalAmount)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNavigateToCheckout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir a Checkout ($itemCount items)")
            }
        }
    }
}
