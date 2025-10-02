package com.example.pokeapitest.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pokeapitest.R
import com.example.pokeapitest.data.database.Pokemon.PokemonEntity
import com.example.pokeapitest.ui.theme.PokeApiTestTheme
import com.example.pokeapitest.utils.ConnectionStatus
import com.example.pokeapitest.utils.Screen
import com.example.pokeapitest.utils.Screen.Companion.NAME_DETAIL
import com.example.pokeapitest.utils.Screen.Companion.ROUTE_DETAIL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeApiTestTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomBar(navController = navController) },
                    topBar = { PokemonTopAppBar() }
                ) { innerPadding ->
                    NavigationMain(innerPadding, navController)
                }
            }
        }
    }
}

@Composable
fun NavigationMain(innerPadding: PaddingValues, navController: NavHostController) {
    Box(modifier = Modifier.padding(innerPadding)) {
        NavHost(
            navController = navController,
            startDestination = Screen.PokemonList.route
        ) {
            composable(Screen.PokemonList.route) {
                PokemonListScreen(
                    viewModel = hiltViewModel(),
                    onPokemonClick = { pokemonId ->
                        navController.navigate(
                            Screen.PokemonDetail.createRoute(
                                pokemonId
                            )
                        )
                    }
                )
            }
            composable(Screen.PokemonRandom.route) {
                RandomPokemonScreen()
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(onPokemonClick = { pokemonId ->
                    navController.navigate(
                        Screen.PokemonDetail.createRoute(
                            pokemonId
                        )
                    )
                })
            }
            composable(
                route = Screen.PokemonDetail.route + ROUTE_DETAIL,
                arguments = listOf(navArgument(NAME_DETAIL) {
                    type = NavType.IntType
                })
            ) {
                PokemonDetailScreen(viewModel = hiltViewModel())
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    viewModel: PokemonViewModel = hiltViewModel(),
    onPokemonClick: (Int) -> Unit
) {
    val pokemonPagingItems = viewModel.pokemonPagingFlow.collectAsLazyPagingItems()
    val networkStatus by viewModel.networkStatus.collectAsState()
    val isRefreshing = pokemonPagingItems.loadState.refresh == LoadState.Loading

    LaunchedEffect(networkStatus) {
        if (networkStatus is ConnectionStatus.Connected) {
            if (pokemonPagingItems.loadState.refresh is LoadState.Error) {
                pokemonPagingItems.retry()
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { pokemonPagingItems.refresh() }
    ) {
        Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
            if (pokemonPagingItems.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator()
            }

            if (pokemonPagingItems.loadState.refresh is LoadState.Error) {
                Text(text = stringResource(R.string.error_loading_data))
            }

            if (pokemonPagingItems.itemCount > 0) {
                LazyColumn {
                    items(pokemonPagingItems.itemCount) { index ->
                        val pokemon = pokemonPagingItems[index]
                        pokemon?.let {
                            PokemonItem(
                                pokemon = it,
                                onClick = { onPokemonClick(it.id) },
                                onToggleFavorite = { pokemonId, isFavorite ->
                                    viewModel.toggleFavorite(pokemonId, isFavorite)
                                })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PokemonItem(
    pokemon: PokemonEntity,
    onClick: () -> Unit,
    onToggleFavorite: (Int, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(dimensionResource(R.dimen.spacer_height)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        GlideImage(
            model = pokemon.defaultSpriteUrl,
            contentDescription = pokemon.name,
            modifier = Modifier.size(dimensionResource(R.dimen.glide_size))
        )
        Text(text = pokemon.name)
        FavoriteButton(
            isFavorite = pokemon.isFavorite,
            onToggleFavorite = { onToggleFavorite(pokemon.id, !pokemon.isFavorite) },
            modifier = Modifier.padding(end = dimensionResource(R.dimen.spacer_height))
        )
    }
}

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onToggleFavorite() },
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = stringResource(R.string.toggle_favorite_description),
            tint = if (isFavorite) Color.Red else Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokeApiTestTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomBar(navController = navController) },
            topBar = { PokemonTopAppBar() }
        ) { innerPadding ->
            NavigationMain(innerPadding, navController)
        }
    }
}