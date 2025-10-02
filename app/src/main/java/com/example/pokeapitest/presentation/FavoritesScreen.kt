package com.example.pokeapitest.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pokeapitest.R

@Composable
fun FavoritesScreen(
    viewModel: PokemonViewModel = hiltViewModel(),
    onPokemonClick: (Int) -> Unit
) {
    val favoritePokemons by viewModel.favoritePokemons.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        if (favoritePokemons.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.no_favorite_pokemon))
            }
        } else {
            LazyColumn {
                items(favoritePokemons.size) { index ->
                    val pokemon = favoritePokemons[index]
                    PokemonItem(
                        pokemon = pokemon,
                        onClick = { onPokemonClick(pokemon.id) },
                        onToggleFavorite = { pokemonId, isFavorite ->
                            viewModel.toggleFavorite(pokemonId, isFavorite)
                        }
                    )
                }
            }
        }
    }
}