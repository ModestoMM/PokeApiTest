package com.example.pokeapitest.data.database.Pokemon

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    @Query("SELECT * FROM pokemon_table")
    fun getAllPokemon(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon_table ORDER BY id ASC")
    fun getPagedPokemons(): PagingSource<Int, PokemonEntity>

    @Query("SELECT * FROM pokemon_table WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoritePokemons(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon_table ORDER BY id DESC LIMIT 1")
    fun getLastPokemonId(): PokemonEntity?

    @Query("UPDATE pokemon_table SET isFavorite = :isFavorite WHERE id = :pokemonId")
    suspend fun updateFavorite(pokemonId: Int, isFavorite: Boolean)

    @Query("SELECT * FROM pokemon_table WHERE id = :pokemonId")
    fun getPokemonById(pokemonId: Int): Flow<PokemonEntity>

    @Query("DELETE FROM pokemon_table")
    suspend fun clearAll()
}