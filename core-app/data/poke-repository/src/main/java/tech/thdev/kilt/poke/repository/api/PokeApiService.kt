package tech.thdev.kilt.poke.repository.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tech.thdev.kilt.poke.repository.model.Pokemon
import tech.thdev.kilt.poke.repository.model.PokemonListResponse

/**
 * Retrofit API service for PokeAPI
 *
 * Base URL: https://pokeapi.co/api/v2/
 */
interface PokeApiService {

  /**
   * Get a list of Pokemon with pagination
   *
   * @param limit Number of Pokemon to fetch (default: 20, max: 100000)
   * @param offset Starting point for pagination (default: 0)
   * @return Pokemon list response with pagination info
   */
  @GET("pokemon")
  suspend fun getPokemonList(
    @Query("limit") limit: Int = 20,
    @Query("offset") offset: Int = 0
  ): PokemonListResponse

  /**
   * Get detailed information about a specific Pokemon
   *
   * @param id Pokemon ID or name
   * @return Detailed Pokemon information
   */
  @GET("pokemon/{id}")
  suspend fun getPokemon(
    @Path("id") id: String
  ): Pokemon

  /**
   * Get detailed information about a specific Pokemon by ID
   *
   * @param id Pokemon ID
   * @return Detailed Pokemon information
   */
  @GET("pokemon/{id}")
  suspend fun getPokemonById(
    @Path("id") id: Int
  ): Pokemon
}
