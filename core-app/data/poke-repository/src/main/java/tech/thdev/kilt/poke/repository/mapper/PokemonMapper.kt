package tech.thdev.kilt.poke.repository.mapper

import tech.thdev.kilt.poke.api.model.PokemonEntity
import tech.thdev.kilt.poke.api.model.PokemonListEntity
import tech.thdev.kilt.poke.repository.model.Pokemon
import tech.thdev.kilt.poke.repository.model.PokemonListResponse

/**
 * Mapper functions to convert API models to Entity models
 */

/**
 * Converts Pokemon API model to PokemonEntity
 */
fun Pokemon.toEntity(): PokemonEntity {
  return PokemonEntity(
    id = id,
    name = name,
    height = height,
    weight = weight,
    baseExperience = baseExperience,
    sprites = PokemonEntity.PokemonSprites(
      frontDefault = sprites.frontDefault,
      frontShiny = sprites.frontShiny,
      backDefault = sprites.backDefault,
      other = sprites.other?.let { other ->
        PokemonEntity.PokemonSpritesOther(
          officialArtwork = other.officialArtwork?.let { artwork ->
            PokemonEntity.PokemonOfficialArtwork(
              frontDefault = artwork.frontDefault
            )
          }
        )
      }
    ),
    types = types.map { type ->
      PokemonEntity.PokemonType(
        slot = type.slot,
        type = PokemonEntity.PokemonTypeInfo(
          name = type.type.name,
          url = type.type.url
        )
      )
    },
    stats = stats.map { stat ->
      PokemonEntity.PokemonStat(
        baseStat = stat.baseStat,
        effort = stat.effort,
        stat = PokemonEntity.PokemonStatInfo(
          name = stat.stat.name,
          url = stat.stat.url
        )
      )
    }
  )
}

/**
 * Converts PokemonListResponse API model to PokemonListEntity
 */
fun PokemonListResponse.toEntity(): PokemonListEntity {
  return PokemonListEntity(
    count = count,
    next = next,
    previous = previous,
    results = results.map { item ->
      PokemonListEntity.PokemonListItem(
        name = item.name,
        url = item.url
      )
    }
  )
}