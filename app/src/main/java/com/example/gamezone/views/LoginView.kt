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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamezone.viewModels.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginView(
    vm: LoginViewModel = viewModel()) {
    val state = vm.estado.collectAsState().value
    val errors = vm.errores.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
