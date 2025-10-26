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

@Composable
fun BienvenidaView(
    vm: BienvenidaViewModel = viewModel(),

    @DrawableRes imageRes: Int? = R.drawable.gamezonelogo
) {
    val titulo = vm.texto.collectAsState().value


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "¡Bienvenido a GameZone!",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))


        if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Logo GameZone",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Placeholder",
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Agrega una imagen a /res/drawable y pásala por imageRes")
        }

        Spacer(modifier = Modifier.height(16.dp))


        Text(
            text = "El lugar donde puedes encontrar los videojuegos que estás buscando.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )


    }
}


