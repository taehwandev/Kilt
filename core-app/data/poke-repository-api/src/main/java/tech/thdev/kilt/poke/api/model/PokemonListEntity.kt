package tech.thdev.kilt.poke.api.model

/**
 * Pokemon list response from PokeAPI
 */
data class PokemonListEntity(
  val count: Int,
  val next: String?,
  val previous: String?,
  val results: List<PokemonListItem>
) {

  data class PokemonListItem(
    val name: String,
    val url: String
  ) {
    /**
     * Extracts Pokemon ID from the URL
     * URL format: https://pokeapi.co/api/v2/pokemon/{id}/
     */
    val id: Int
      get() = url.trimEnd('/').split('/').last().toIntOrNull() ?: 0
  }
}