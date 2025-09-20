package tech.thdev.kilt.poke.repository.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Pokemon list response from PokeAPI
 */
@Serializable
data class PokemonListResponse(
  @SerialName("count")
  val count: Int,

  @SerialName("next")
  val next: String?,

  @SerialName("previous")
  val previous: String?,

  @SerialName("results")
  val results: List<PokemonListItem>
)

@Serializable
data class PokemonListItem(
  @SerialName("name")
  val name: String,

  @SerialName("url")
  val url: String
) {
  /**
   * Extracts Pokemon ID from the URL
   * URL format: https://pokeapi.co/api/v2/pokemon/{id}/
   */
  val id: Int
    get() = url.trimEnd('/').split('/').last().toIntOrNull() ?: 0
}
