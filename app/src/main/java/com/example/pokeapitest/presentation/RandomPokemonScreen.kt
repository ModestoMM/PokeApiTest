package com.example.pokeapitest.presentation

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pokeapitest.data.database.Pokemon.PokemonEntity

@Composable
fun RandomPokemonScreen(viewModel: PokemonViewModel = hiltViewModel()) {
    val randomPokemon by viewModel.randomPokemon.collectAsState()
    val showAlert by viewModel.showAlert.collectAsState()
    val context = LocalContext.current

    // Lista de permisos de ubicación
    val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // Crear el lanzador de permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (fineLocationGranted || coarseLocationGranted) {
                // Si se concedió al menos un permiso, iniciar la búsqueda de ubicación
                viewModel.startLocationUpdates()
            } else {
                // Si se denegaron los permisos, mostrar un mensaje al usuario
                // Puedes implementar una lógica para mostrar un SnackBar, un AlertDialog, etc.
            }
        }
    )

    // Lanzar la solicitud de permisos al entrar a la pantalla
    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissionsToRequest)
    }

    // Observar el Flow de vibración
    LaunchedEffect(Unit) {
        viewModel.vibrateFlow.collect {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        500,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        }
    }

    // Diálogo de alerta
    if (showAlert) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAlert() },
            title = { Text("Pokémon encontrado") },
            text = { Text("¡Un nuevo Pokémon ha aparecido!") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.hideAlert()
                    viewModel.getNewRandomPokemon()
                }) {
                    Text("Capturar")
                }
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Muestra el Pokémon aleatorio si está disponible
        randomPokemon?.let { pokemon ->
            PokemonDetails(pokemon)
        } ?: run {
            // Muestra un indicador de carga o un mensaje si no hay Pokémon
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Buscando Pokémon...")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para obtener un Pokémon aleatorio sin desplazamiento
        Button(onClick = { viewModel.getNewRandomPokemon() }) {
            Text("Encontrar Pokémon al azar")
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PokemonDetails(pokemon: PokemonEntity) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GlideImage(
            model = pokemon.defaultSpriteUrl,
            contentDescription = pokemon.name,
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Nombre: ${pokemon.name}", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Tipo: ${pokemon.types}", style = MaterialTheme.typography.bodyMedium)
    }
}
