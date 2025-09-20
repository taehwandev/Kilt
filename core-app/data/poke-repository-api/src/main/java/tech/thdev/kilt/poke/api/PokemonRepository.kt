package tech.thdev.kilt.poke.api

import tech.thdev.kilt.poke.api.model.PokemonEntity
import tech.thdev.kilt.poke.api.model.PokemonListEntity

/**
 * Repository interface for Pokemon data access
 *
 * This interface defines the contract for accessing Pokemon data,
 * abstracting away the data source implementation details.
 */
interface PokemonRepository {

  /**
   * Fetches a paginated list of Pokemon
   *
   * @param limit Number of Pokemon to fetch (default: 20)
   * @param offset Starting point for pagination (default: 0)
   * @return Result containing Pokemon list response or error
   */
  suspend fun getPokemonList(
    limit: Int = 20,
    offset: Int = 0
  ): PokemonListEntity

  /**
   * Fetches detailed information about a specific Pokemon
   *
   * @param id Pokemon ID
   * @return Result containing Pokemon details or error
   */
  suspend fun getPokemonById(id: Int): PokemonEntity

  /**
   * Fetches detailed information about a specific Pokemon by name
   *
   * @param name Pokemon name (case-insensitive)
   * @return Result containing Pokemon details or error
   */
  suspend fun getPokemonByName(name: String): PokemonEntity

  /**
   * Fetches multiple Pokemon details by their IDs
   *
   * @param ids List of Pokemon IDs
   * @return Result containing list of Pokemon details or error
   */
  suspend fun getPokemonByIds(ids: List<Int>): List<PokemonEntity>
}
