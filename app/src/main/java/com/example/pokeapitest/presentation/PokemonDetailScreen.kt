package com.example.pokeapitest.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pokeapitest.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PokemonDetailScreen(viewModel: PokemonViewModel = hiltViewModel()) {
    val pokemon by viewModel.pokemon.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.spacer_height)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        pokemon?.let {
            GlideImage(
                model = it.defaultSpriteUrl,
                contentDescription = it.name,
                modifier = Modifier.size(dimensionResource(R.dimen.glide_size))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.id_pokemon, it.id))
            Text(text = stringResource(R.string.pokemon_name, it.name))
            Text(text = stringResource(R.string.pokemon_height, it.height))
            Text(text = stringResource(R.string.pokemon_weight, it.weight))
            Text(text = stringResource(R.string.pokemon_types, it.types))
            Spacer(modifier = Modifier.height(16.dp))
            FavoriteButton(
                isFavorite = it.isFavorite,
                onToggleFavorite = { viewModel.toggleFavorite(it.id, !it.isFavorite) }
            )
        } ?: run {
            Text(stringResource(R.string.loading))
        }
    }
}