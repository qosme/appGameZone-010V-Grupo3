package com.example.gamezone.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gamezone.viewModels.CheckoutViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutView(
    onOrderComplete: () -> Unit = {},
    userEmail: String,
    vm: CheckoutViewModel = hiltViewModel()
) {
    val cartItems = vm.cartItems.collectAsState().value
    val totalAmount = vm.totalAmount.collectAsState().value
    val isLoading = vm.isLoading.collectAsState().value
    val orderResult = vm.orderResult.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    LaunchedEffect(userEmail) {
        Log.d("CheckoutView", "Loading cart items for email: $userEmail")
        vm.loadCartItems(userEmail)
    }


    LaunchedEffect(orderResult) {
        orderResult?.let { result ->
            Log.d("CheckoutView", "Order result: $result")
            scope.launch {
                snackbarHostState.showSnackbar(result)
                if (result.contains("exitoso")) {
                    onOrderComplete()
                }
            }
        }
    }


    var shippingAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Finalizar Compra",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) }
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                if (cartItems.isEmpty()) {
                    Text("No items in the cart.")
                } else {
                    cartItems.forEach { item ->
                        Text(
                            text = "Game: ${item.gameName}, Quantity: ${item.quantity}, Price: $${item.price * item.quantity}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Resumen del Pedido",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )


                        if (cartItems.isNotEmpty()) {
                            cartItems.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.gameName} x${item.quantity}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "$${String.format("%.0f", item.price * item.quantity)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Text("Total Amount: $${totalAmount}")
                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${String.format("%.0f", totalAmount)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }


                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Información de Envío",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = shippingAddress,
                            onValueChange = { shippingAddress = it },
                            label = { Text("Dirección de Envío") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }


                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Información de Pago",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = { paymentMethod = it },
                            label = { Text("Método de Pago") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Ej: Tarjeta de Crédito, PayPal, etc.") }
                        )
                    }
                }


                Button(
                    onClick = {
                        if (shippingAddress.isNotBlank() && paymentMethod.isNotBlank()) {

                            vm.placeOrder(userEmail, shippingAddress, paymentMethod)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Por favor completa todos los campos")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = cartItems.isNotEmpty() && shippingAddress.isNotBlank() && paymentMethod.isNotBlank()
                ) {
                    Text("Confirmar Pedido")
                }
            }
        }
    }
}
