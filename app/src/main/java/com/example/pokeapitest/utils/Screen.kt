package com.example.pokeapitest.utils

sealed class Screen(val route: String) {
    object PokemonList : Screen("pokemon_list")
    object PokemonRandom : Screen("pokemon_random")
    object Favorites : Screen("favorites_list")
    object PokemonDetail : Screen("pokemon_detail") {
        fun createRoute(pokemonId: Int) = "pokemon_detail/$pokemonId"
    }
}