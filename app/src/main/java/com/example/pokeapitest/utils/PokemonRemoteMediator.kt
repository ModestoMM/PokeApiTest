package com.example.pokeapitest.utils

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pokeapitest.data.database.Pokemon.PokemonDao
import com.example.pokeapitest.data.database.Pokemon.PokemonEntity
import com.example.pokeapitest.data.database.PokemonDatabase
import com.example.pokeapitest.repository.PokemonApiService
import java.io.InvalidObjectException

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val apiService: PokemonApiService,
    private val pokemonDao: PokemonDao,
    private val pokemonDatabase: PokemonDatabase
) : RemoteMediator<Int, PokemonEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        return try {
            val offset = when (loadType) {
                LoadType.REFRESH -> 0 // Siempre comienza en 0 al refrescar
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastPokemon = pokemonDatabase.withTransaction {
                        pokemonDao.getLastPokemonId()
                    } ?: return MediatorResult.Success(endOfPaginationReached = true)
                    lastPokemon.id
                }
            }

            // Llamada a la API para obtener la lista paginada de Pokémon
            val response = apiService.getListPokemon(
                limit = state.config.pageSize,
                offset = offset
            )

            // Obtener los detalles de cada Pokémon y procesarlos
            val pokemonDetails = response.results.map { pokemon ->
                apiService.getPokemonDetails(pokemon.name)
            }

            // Transacción de Room para garantizar la atomicidad de las operaciones
            pokemonDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pokemonDao.clearAll() // Limpiar la tabla solo al refrescar
                }

                // Convertir los detalles de la API en entidades y persistirlos
                pokemonDetails.forEach { details ->
                    val types = details.types.joinToString(",") { it.type.name }
                    val entity = PokemonEntity(
                        id = details.id,
                        name = details.name,
                        imageUrl = details.sprites.front_default ?: "",
                        defaultSpriteUrl = details.sprites.front_default ?: "",
                        height = details.height,
                        weight = details.weight,
                        types = types
                    )
                    pokemonDao.insertPokemon(entity)
                }
            }

            MediatorResult.Success(endOfPaginationReached = pokemonDetails.isEmpty())
        } catch (e: Exception) {
            // Loguear el error para diagnosticar
            Log.e("RemoteMediator", "Error al cargar datos: ${e.message}", e)
            MediatorResult.Error(e)
        } as MediatorResult
    }
}