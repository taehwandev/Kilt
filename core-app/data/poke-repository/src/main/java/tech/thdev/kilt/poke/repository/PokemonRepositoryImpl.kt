package tech.thdev.kilt.poke.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import tech.thdev.kilt.annotations.KiltGenerateModule
import tech.thdev.kilt.poke.api.PokemonRepository
import tech.thdev.kilt.poke.api.model.PokemonEntity
import tech.thdev.kilt.poke.api.model.PokemonListEntity
import tech.thdev.kilt.poke.repository.api.PokeApiService
import tech.thdev.kilt.poke.repository.mapper.toEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PokemonRepository using PokeAPI
 *
 * This class uses the @KiltGenerateModule annotation to automatically
 * generate a Hilt module that binds this implementation to the PokemonRepository interface.
 *
 * The @Singleton annotation ensures that only one instance of this repository
 * exists throughout the application lifecycle.
 */
@Singleton
@KiltGenerateModule
class PokemonRepositoryImpl @Inject constructor(
  private val apiService: PokeApiService,
) : PokemonRepository {

  override suspend fun getPokemonList(
    limit: Int,
    offset: Int
  ): PokemonListEntity {
    val response = apiService.getPokemonList(limit, offset)
    return response.toEntity()
  }

  override suspend fun getPokemonById(id: Int): PokemonEntity {
    val pokemon = apiService.getPokemonById(id)
    return pokemon.toEntity()
  }

  override suspend fun getPokemonByName(name: String): PokemonEntity {
    val pokemon = apiService.getPokemon(name.lowercase())
    return pokemon.toEntity()
  }

  override suspend fun getPokemonByIds(ids: List<Int>): List<PokemonEntity> {
    return coroutineScope {
      val deferredPokemon = ids.map { id ->
        async { apiService.getPokemonById(id) }
      }
      val pokemonList = deferredPokemon.awaitAll()
      pokemonList.map { it.toEntity() }
    }
  }
}
