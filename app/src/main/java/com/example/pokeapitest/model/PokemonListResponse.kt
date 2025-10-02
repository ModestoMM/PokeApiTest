package com.example.pokeapitest.model

data class PokemonListResponse(
    val results: List<Pokemon>,
    val next: String?,
    val previous: String?
)