package tech.thdev.kilt.poke.repository.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Pokemon data model from PokeAPI
 */
@Serializable
data class Pokemon(
  @SerialName("id")
  val id: Int,

  @SerialName("name")
  val name: String,

  @SerialName("height")
  val height: Int,

  @SerialName("weight")
  val weight: Int,

  @SerialName("base_experience")
  val baseExperience: Int?,

  @SerialName("sprites")
  val sprites: PokemonSprites,

  @SerialName("types")
  val types: List<PokemonType>,

  @SerialName("stats")
  val stats: List<PokemonStat>
)

@Serializable
data class PokemonSprites(
  @SerialName("front_default")
  val frontDefault: String?,

  @SerialName("front_shiny")
  val frontShiny: String?,

  @SerialName("back_default")
  val backDefault: String?,

  @SerialName("other")
  val other: PokemonSpritesOther?
)

@Serializable
data class PokemonSpritesOther(
  @SerialName("official-artwork")
  val officialArtwork: PokemonOfficialArtwork?
)

@Serializable
data class PokemonOfficialArtwork(
  @SerialName("front_default")
  val frontDefault: String?
)

@Serializable
data class PokemonType(
  @SerialName("slot")
  val slot: Int,

  @SerialName("type")
  val type: PokemonTypeInfo
)

@Serializable
data class PokemonTypeInfo(
  @SerialName("name")
  val name: String,

  @SerialName("url")
  val url: String
)

@Serializable
data class PokemonStat(
  @SerialName("base_stat")
  val baseStat: Int,

  @SerialName("effort")
  val effort: Int,

  @SerialName("stat")
  val stat: PokemonStatInfo
)

@Serializable
data class PokemonStatInfo(
  @SerialName("name")
  val name: String,

  @SerialName("url")
  val url: String
)
