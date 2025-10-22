package com.example.gamezone.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamezone.viewModels.UsuarioViewModel
import kotlinx.coroutines.launch

/**
 * CLASE 3: Formularios + Validaciones + Componentes interactivos
 * - TextField con error (nombre, email, edad).
 * - Dropdown (rol).
 * - Checkbox (acepta términos), Switch (suscripción).
 * - Botón de enviar habilitado sólo si es válido.
 * - Snackbar para feedback y reset de formulario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroView(
    vm: UsuarioViewModel = viewModel()
) {
    val state = vm.estado.collectAsState().value
    val errors = vm.errores.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var rolMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Formulario con validación", style = MaterialTheme.typography.titleLarge)

            // NOMBRE
            OutlinedTextField(
                value = state.nombre,
                onValueChange = vm::onNombreChange,
                label = { Text("Nombre completo *") },
                isError = errors.nombre != null,
                supportingText = { if (errors.nombre != null) Text(errors.nombre!!) },
                modifier = Modifier.fillMaxWidth()
            )

            // EMAIL
            OutlinedTextField(
                value = state.correo,
                onValueChange = vm::onCorreoChange,
                label = { Text("Correo *") },
                isError = errors.correo != null,
                supportingText = { if (errors.correo != null) Text(errors.correo!!) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            // Clave
            OutlinedTextField(
                value = state.clave,
                onValueChange = vm::onClaveChange,
                label = { Text(text = "Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = state.errores.clave != null,
                supportingText = {
                    state.errores.clave?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Telefono
            OutlinedTextField(
                value = state.telefono,
                onValueChange = vm::onTelefonoChange,
                label = { Text("Teléfono *") },
                isError = errors.telefono != null,
                supportingText = { if (errors.telefono != null) Text(errors.telefono!!) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // ACEPTA TÉRMINOS + SWITCH
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Checkbox(
                        checked = state.aceptaTerminos,
                        onCheckedChange = vm::onAceptarTerminosChange
                    )
                    Column {
                        Text("Acepto términos *")
                        if (errors.aceptaTerminos != null) {
                            Text(errors.aceptaTerminos!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // BOTONES
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val ok = vm.validate()
                        if (ok) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Formulario válido. Enviando…")
                            }
                            // Simular envío y limpiar
                            vm.reset()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Revisa los campos resaltados.")
                            }
                        }
                    }
                ) {
                    Text("Enviar")
                }
                OutlinedButton(onClick = { vm.reset() }) {
                    Text("Limpiar")
                }
            }
        }
    }
}