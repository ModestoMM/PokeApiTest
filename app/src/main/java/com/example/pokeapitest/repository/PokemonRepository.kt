package com.example.pokeapitest.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pokeapitest.data.database.Pokemon.PokemonDao
import com.example.pokeapitest.data.database.Pokemon.PokemonEntity
import com.example.pokeapitest.data.database.PokemonDatabase
import com.example.pokeapitest.utils.PokemonRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//Se encarga de realizar las operaciones tanto para la red como parala base de datos room.
class PokemonRepository @Inject constructor(
    private val apiService: PokemonApiService,
    private val pokemonDao: PokemonDao,
    private val pokemonDatabase: PokemonDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getPokemonList(): Flow<PagingData<PokemonEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 25, prefetchDistance = 5),
            // Asegúrate de que esto apunta a la fuente de datos de Room
            pagingSourceFactory = { pokemonDao.getPagedPokemons() },
            remoteMediator = PokemonRemoteMediator(apiService, pokemonDao, pokemonDatabase)
        ).flow
    }

    fun getPokemonById(pokemonId: Int): Flow<PokemonEntity> {
        return pokemonDao.getPokemonById(pokemonId)
    }

    fun getFavoritePokemons(): Flow<List<PokemonEntity>> {
        return pokemonDao.getFavoritePokemons()
    }

    suspend fun updateFavorite(pokemonId: Int, isFavorite: Boolean) {
        pokemonDao.updateFavorite(pokemonId, isFavorite)
    }

    suspend fun getRandomPokemon(): PokemonEntity {
        val randomId = (1..1025).random()
        val detailsResponse = apiService.getPokemonDetailsById(randomId)
        // Mapea la respuesta a PokemonEntity
        val types = detailsResponse.types.joinToString(",") { it.type.name }
        return PokemonEntity(
            id = detailsResponse.id,
            name = detailsResponse.name,
            imageUrl = detailsResponse.sprites.front_default ?: "",
            defaultSpriteUrl = detailsResponse.sprites.front_default ?: "",
            height = detailsResponse.height,
            weight = detailsResponse.weight,
            types = types
        )
    }
}