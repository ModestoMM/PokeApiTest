package com.example.pokeapitest.model

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val types: List<PokemonType>
)

data class Sprites(
    val front_default: String?
)

data class PokemonType(
    val type: TypeName
)

data class TypeName(
    val name: String
)