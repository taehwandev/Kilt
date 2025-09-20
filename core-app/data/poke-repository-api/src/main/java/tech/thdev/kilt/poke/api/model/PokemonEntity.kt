package tech.thdev.kilt.poke.api.model

/**
 * Pokemon data model from PokeAPI
 */
data class PokemonEntity(
  val id: Int,
  val name: String,
  val height: Int,
  val weight: Int,
  val baseExperience: Int?,
  val sprites: PokemonSprites,
  val types: List<PokemonType>,
  val stats: List<PokemonStat>
) {

  data class PokemonSprites(
    val frontDefault: String?,
    val frontShiny: String?,
    val backDefault: String?,
    val other: PokemonSpritesOther?
  )

  data class PokemonSpritesOther(
    val officialArtwork: PokemonOfficialArtwork?
  )

  data class PokemonOfficialArtwork(
    val frontDefault: String?
  )

  data class PokemonType(
    val slot: Int,
    val type: PokemonTypeInfo
  )

  data class PokemonTypeInfo(
    val name: String,
    val url: String
  )

  data class PokemonStat(
    val baseStat: Int,
    val effort: Int,
    val stat: PokemonStatInfo
  )

  data class PokemonStatInfo(
    val name: String,
    val url: String
  )
}
