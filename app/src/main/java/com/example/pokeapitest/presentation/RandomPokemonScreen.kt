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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pokeapitest.R
import com.example.pokeapitest.data.database.Pokemon.PokemonEntity

@Composable
fun RandomPokemonScreen(viewModel: PokemonViewModel = hiltViewModel()) {
    val randomPokemon by viewModel.randomPokemon.collectAsState()


    CheckPermission(viewModel)

    VibrationStatus(viewModel)

    PokemonAlertFound(viewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        randomPokemon?.let { pokemon ->
            PokemonDetails(pokemon)
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.looking_pokemon))
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.more_spacer_height)))

        Button(onClick = { viewModel.getNewRandomPokemon() }) {
            Text(stringResource(R.string.find_random_pokemon))
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
            modifier = Modifier.size(dimensionResource(R.dimen.glide_size))
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_height)))
        Text(text = stringResource(R.string.pokemon_name, pokemon.name), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_height)))
        Text(text = stringResource(R.string.pokemon_types, pokemon.types), style = MaterialTheme.typography.bodyMedium)
    }
}


@Composable
private fun CheckPermission(viewModel: PokemonViewModel) {
    // Crear el lanzador de permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (fineLocationGranted || coarseLocationGranted) {
                viewModel.startLocationUpdates()
            }
        }
    )
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

@Composable
private fun VibrationStatus(viewModel: PokemonViewModel) {
    val context = LocalContext.current
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
}

@Composable
private fun PokemonAlertFound(viewModel: PokemonViewModel) {
    val showAlert by viewModel.showAlert.collectAsState()
    if (showAlert) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAlert() },
            title = { Text(stringResource(R.string.pokemon_found)) },
            text = { Text(stringResource(R.string.new_pokemon)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.hideAlert()
                    viewModel.getNewRandomPokemon()
                }) {
                    Text(text = stringResource(R.string.pokemon_capture))
                }
            }
        )
    }
}
