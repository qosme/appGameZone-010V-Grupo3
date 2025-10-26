package com.example.gamezone.views

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamezone.viewModels.BienvenidaViewModel
import com.example.gamezone.R

/**
 * DEMO COMPONENTES BÁSICOS
 *
 * Objetivo didáctico:
 * 1) Layout vertical con Column + espaciados (Spacer) y padding (Modifier).
 * 2) Texto con jerarquía tipográfica (MaterialTheme.typography).
 * 3) Fila horizontal con Row (alineación y distribución).
 * 4) Card que contiene imagen + texto (y cómo redondear esquinas).
 * 5) Image desde drawable. Alternativa con Icon de Material3.
 * 6) Button que modifica un estado local (contador).
 *
 * Notas MVVM:
 * - Tomamos un "título" desde un ViewModel (estado de pantalla).
 * - El contador es estado de UI efímero -> lo guardamos con rememberSaveable.
 */
@Composable
fun BienvenidaView(
    vm: BienvenidaViewModel = viewModel(),
    // Cambia este recurso por el que tengas en drawable (ej: R.drawable.compose_logo)
    @DrawableRes imageRes: Int? = R.drawable.gamezonelogo
) {
    val titulo = vm.texto.collectAsState().value

    // Column con solo el título, la descripción y la imagen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // margin interno de la pantalla
        verticalArrangement = Arrangement.Center, // Centrado verticalmente
        horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontalmente
    ) {
        // --- TITULO (con un título grande) ---
        Text(
            text = "¡Bienvenido a GameZone!",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre el título y la imagen

        // --- IMAGEN ---
        if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Logo GameZone",
                contentScale = ContentScale.Crop, // recorta para llenar
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp)) // bordes redondeados
            )
        } else {
            // Placeholder con Icon si no hay imagen
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Placeholder",
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Agrega una imagen a /res/drawable y pásala por imageRes")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre la imagen y la descripción

        // --- DESCRIPCIÓN (breve) ---
        Text(
            text = "El lugar donde puedes encontrar los videojuegos que estás buscando.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )


    }
}


