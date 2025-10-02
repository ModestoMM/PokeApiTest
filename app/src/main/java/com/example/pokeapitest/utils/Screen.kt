package com.example.pokeapitest.utils

//Se encarga de definir la estructura de la navegación dentro de la aplicación.
sealed class Screen(val route: String) {

    companion object {
        const val  ROUTE_DETAIL = "/{pokemonId}"
        const val NAME_DETAIL = "pokemonId"
        private const val POKEMON_LIST = "pokemon_list"
        private const val POKEMON_RANDOM = "pokemon_random"
        private const val POKEMON_FAVORITE = "favorites_list"
        private const val POKEMON_DETAIL = "pokemon_detail"
    }
    object PokemonList : Screen(POKEMON_LIST)
    object PokemonRandom : Screen(POKEMON_RANDOM)
    object Favorites : Screen(POKEMON_FAVORITE)
    object PokemonDetail : Screen(POKEMON_DETAIL) {
        fun createRoute(pokemonId: Int) = "$POKEMON_DETAIL/$pokemonId"
    }
}