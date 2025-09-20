package tech.thdev.kilt.feature.main

import tech.thdev.kilt.poke.api.model.PokemonListEntity

/**
 * UI state for the main Pokemon list screen
 */
data class MainUiState(
  val isLoading: Boolean = false,
  val pokemonList: List<PokemonItemUiState> = emptyList(),
  val error: String? = null,
  val isRefreshing: Boolean = false,
  val hasMore: Boolean = true,
  val nextUrl: String? = null
) {
  /**
   * Helper function to check if the screen is in initial loading state
   */
  fun isInitialLoading(): Boolean = isLoading && pokemonList.isEmpty()

  /**
   * Helper function to check if there's an error with empty content
   */
  fun hasErrorWithEmptyContent(): Boolean = error != null && pokemonList.isEmpty()
}

/**
 * UI state for individual Pokemon items in the list
 */
data class PokemonItemUiState(
  val id: Int,
  val name: String,
  val imageUrl: String?,
  val types: List<String> = emptyList()
) {
  /**
   * Format Pokemon name for display (capitalize first letter)
   */
  val displayName: String
    get() = name.replaceFirstChar { it.uppercase() }
}

/**
 * Extension function to convert PokemonListEntity to UI state
 */
fun PokemonListEntity.toUiState(): List<PokemonItemUiState> {
  return results.map { pokemon ->
    PokemonItemUiState(
      id = pokemon.id,
      name = pokemon.name,
      imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.id}.png"
    )
  }
}