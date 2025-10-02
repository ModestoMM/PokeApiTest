package com.example.pokeapitest.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pokeapitest.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonTopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        title = {
            Image(
                modifier = modifier.width(160.dp),
                painter = painterResource(R.drawable.pokemon_logo),
                contentDescription = "Pokemon Logo"
            )
        }
    )
}