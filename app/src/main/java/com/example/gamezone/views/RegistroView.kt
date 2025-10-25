package com.example.gamezone.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gamezone.viewModels.UsuarioViewModel
import kotlinx.coroutines.launch

@Composable
fun RegistroView(
    vm: UsuarioViewModel = hiltViewModel(),
    onRegistrationSuccess: () -> Unit = {}
) {
    val state = vm.estado.collectAsState().value
    val errors = vm.errores.collectAsState().value
    val isLoading = vm.isLoading.collectAsState().value
    val registrationResult = vm.registrationResult.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle registration result
    LaunchedEffect(registrationResult) {
        registrationResult?.let { result ->
            scope.launch {
                snackbarHostState.showSnackbar(result)
                if (result == "Usuario registrado exitosamente") {
                    onRegistrationSuccess()
                    vm.reset()
                }
            }
        }
    }

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
            Text("Registro de Usuario", style = MaterialTheme.typography.titleLarge)

            // Nombre
            OutlinedTextField(
                value = state.nombre,
                onValueChange = vm::onNombreChange,
                label = { Text("Nombre completo *") },
                isError = errors.nombre != null,
                supportingText = { if (errors.nombre != null) Text(errors.nombre!!) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )

            // Email
            OutlinedTextField(
                value = state.correo,
                onValueChange = vm::onCorreoChange,
                label = { Text("Correo electrónico *") },
                isError = errors.correo != null,
                supportingText = { if (errors.correo != null) Text(errors.correo!!) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            // Teléfono
            OutlinedTextField(
                value = state.telefono,
                onValueChange = vm::onTelefonoChange,
                label = { Text("Teléfono *") },
                isError = errors.telefono != null,
                supportingText = { if (errors.telefono != null) Text(errors.telefono!!) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            // Contraseña
            OutlinedTextField(
                value = state.clave,
                onValueChange = vm::onClaveChange,
                label = { Text("Contraseña *") },
                isError = errors.clave != null,
                supportingText = { if (errors.clave != null) Text(errors.clave!!) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            // Términos y condiciones
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = state.aceptaTerminos,
                    onCheckedChange = vm::onAceptarTerminosChange
                )
                Text(
                    text = "Acepto los términos y condiciones *",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            if (errors.aceptaTerminos != null) {
                Text(
                    text = errors.aceptaTerminos!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            // Botones
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { vm.register() },
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Registrando..." else "Registrar")
                }
                OutlinedButton(
                    onClick = { vm.reset() }
                ) {
                    Text("Limpiar")
                }
            }
        }
    }
}