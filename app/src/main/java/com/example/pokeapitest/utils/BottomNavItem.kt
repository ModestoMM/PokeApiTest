package com.example.pokeapitest.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

//Tendra la logica para mostrar la pantalla dependiendo la opcion que elija el usuario en el BottomBar.
sealed class BottomNavItem(var title: String, var icon: ImageVector, var screen: Screen) {
    companion object {
        private const val LIST_TITLE = "Listado"
        private const val RANDOM_TITLE = "Listado"
        private const val FAVORITES_TITLE = "Listado"
    }

    object PokemonList : BottomNavItem(LIST_TITLE, Icons.Default.Menu, Screen.PokemonList)
    object PokemonRandom : BottomNavItem(RANDOM_TITLE, Icons.Default.Info, Screen.PokemonRandom)
    object Favorites : BottomNavItem(FAVORITES_TITLE, Icons.Default.Favorite, Screen.Favorites)
}