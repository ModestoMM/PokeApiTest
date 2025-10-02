package com.example.pokeapitest.repository

import com.example.pokeapitest.model.PokemonDetailResponse
import com.example.pokeapitest.model.PokemonListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//Implementara los servicios que utilizaremos en nuestra aplicación
interface PokemonApiService {
    @GET("pokemon")
    suspend fun getListPokemon(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetails(
        @Path("name") name: String
    ): PokemonDetailResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetailsById(@Path("id") id: Int): PokemonDetailResponse
}