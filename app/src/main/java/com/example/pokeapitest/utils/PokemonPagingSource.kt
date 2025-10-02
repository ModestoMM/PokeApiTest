package com.example.pokeapitest.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokeapitest.data.database.Pokemon.PokemonDao
import com.example.pokeapitest.data.database.Pokemon.PokemonEntity
import com.example.pokeapitest.model.Pokemon
import com.example.pokeapitest.repository.PokemonApiService

class PokemonPagingSource(
    private val apiService: PokemonApiService,
    private val pokemonDao: PokemonDao
) : PagingSource<Int, PokemonEntity>() {

    override fun getRefreshKey(state: PagingState<Int, PokemonEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonEntity> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize
            val response = apiService.getListPokemon(limit = limit, offset = offset)

            // Lógica para guardar los detalles en la base de datos
            val pokemonEntities = mutableListOf<PokemonEntity>()
            response.results.forEach { pokemon ->
                val detailsResponse = apiService.getPokemonDetails(pokemon.name)
                val types = detailsResponse.types.joinToString(",") { it.type.name }
                val entity = PokemonEntity(
                    id = detailsResponse.id,
                    name = detailsResponse.name,
                    imageUrl = detailsResponse.sprites.front_default ?: "",
                    defaultSpriteUrl = detailsResponse.sprites.front_default ?: "",
                    height = detailsResponse.height,
                    weight = detailsResponse.weight,
                    types = types
                )
                pokemonDao.insertPokemon(entity) // Persistir en la base de datos
                pokemonEntities.add(entity)
            }

            LoadResult.Page(
                data = pokemonEntities,
                prevKey = if (offset == 0) null else offset - limit,
                nextKey = if (pokemonEntities.isEmpty()) null else offset + limit
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}