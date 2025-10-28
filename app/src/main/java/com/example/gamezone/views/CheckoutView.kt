package com.example.gamezone.views

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
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
    val focusManager = LocalFocusManager.current


    var shippingAddress by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("") }


    LaunchedEffect(userEmail) {
        vm.loadCartItems(userEmail)
    }

    LaunchedEffect(orderResult) {
        orderResult?.let { result ->
            scope.launch {
                snackbarHostState.showSnackbar(result)
                if (result.contains("exitoso")) {
                    shippingAddress = ""
                    selectedPaymentMethod = ""

                    onOrderComplete()
                }
            }
        }
    }


    val paymentOptions = listOf(
        "Webpay",
        "Mach",
        "PayPal",
        "Onepay"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Finalizar Compra",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(16.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { focusManager.clearFocus() },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {


                if (cartItems.isEmpty()) {
                    Text("No hay juegos en el carro.")
                } else {
                    cartItems.forEach { item ->
                        Text(
                            text = "Juego: ${item.gameName}, Cantidad: ${item.quantity}, Precio: $${String.format("%.0f", item.price * item.quantity)}",
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

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

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


                Card(modifier = Modifier.fillMaxWidth()) {
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


                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Información de Pago",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            paymentOptions.forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedPaymentMethod = option }
                                ) {
                                    Checkbox(
                                        checked = selectedPaymentMethod == option,
                                        onCheckedChange = {
                                            selectedPaymentMethod = if (it) option else ""
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(option)
                                }
                            }
                        }
                    }
                }


                Button(
                    onClick = {
                        if (shippingAddress.isNotBlank() && selectedPaymentMethod.isNotBlank()) {
                            vm.placeOrder(userEmail, shippingAddress, selectedPaymentMethod)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Por favor completa todos los campos")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = cartItems.isNotEmpty() && shippingAddress.isNotBlank() && selectedPaymentMethod.isNotBlank()
                ) {
                    Text("Confirmar Pedido")
                }
            }
        }
    }
}
