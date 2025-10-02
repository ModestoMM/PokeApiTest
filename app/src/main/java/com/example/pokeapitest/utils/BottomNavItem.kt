package com.example.pokeapitest.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, var icon: ImageVector, var screen: Screen) {
    object PokemonList : BottomNavItem("Listado", Icons.Default.Menu, Screen.PokemonList)
    object PokemonRandom : BottomNavItem("Random", Icons.Default.Info, Screen.PokemonRandom)
    object Favorites : BottomNavItem("Favoritos", Icons.Default.Favorite, Screen.Favorites)
}