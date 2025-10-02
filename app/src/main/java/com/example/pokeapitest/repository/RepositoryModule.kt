package com.example.pokeapitest.repository

import com.example.pokeapitest.data.database.Pokemon.PokemonDao
import com.example.pokeapitest.data.database.PokemonDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun providePokemonRepository(
        apiService: PokemonApiService,
        pokemonDao: PokemonDao,
        pokemonDatabase: PokemonDatabase
    ): PokemonRepository {
        return PokemonRepository(apiService, pokemonDao,pokemonDatabase) // El constructor del repo no necesita la BD
    }

    @Singleton
    @Provides
    fun provideLocationUpdatesRepository(): LocationUpdatesRepository {
        return LocationUpdatesRepository()
    }
}