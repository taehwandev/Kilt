package tech.thdev.kilt.poke.repository.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import tech.thdev.kilt.poke.repository.model.Pokemon
import tech.thdev.kilt.poke.repository.model.PokemonListItem
import tech.thdev.kilt.poke.repository.model.PokemonListResponse
import tech.thdev.kilt.poke.repository.model.PokemonOfficialArtwork
import tech.thdev.kilt.poke.repository.model.PokemonSprites
import tech.thdev.kilt.poke.repository.model.PokemonSpritesOther
import tech.thdev.kilt.poke.repository.model.PokemonStat
import tech.thdev.kilt.poke.repository.model.PokemonStatInfo
import tech.thdev.kilt.poke.repository.model.PokemonType
import tech.thdev.kilt.poke.repository.model.PokemonTypeInfo

class PokemonMapperTest {

  @Test
  fun `toEntity should convert Pokemon to PokemonEntity correctly`() {
    // Given
    val pokemon = createSamplePokemon()

    // When
    val result = pokemon.toEntity()

    // Then
    assertNotNull(result)
    assertEquals(1, result.id)
    assertEquals("bulbasaur", result.name)
    assertEquals(7, result.height)
    assertEquals(69, result.weight)
    assertEquals(64, result.baseExperience)

    // Verify sprites conversion
    assertNotNull(result.sprites)
    assertEquals("front_default.png", result.sprites.frontDefault)
    assertEquals("front_shiny.png", result.sprites.frontShiny)
    assertEquals("back_default.png", result.sprites.backDefault)

    // Verify official artwork conversion
    assertNotNull(result.sprites.other)
    assertNotNull(result.sprites.other?.officialArtwork)
    assertEquals("official_artwork.png", result.sprites.other?.officialArtwork?.frontDefault)

    // Verify types conversion
    assertEquals(2, result.types.size)
    assertEquals(1, result.types[0].slot)
    assertEquals("grass", result.types[0].type.name)
    assertEquals("https://pokeapi.co/api/v2/type/12/", result.types[0].type.url)

    // Verify stats conversion
    assertEquals(2, result.stats.size)
    assertEquals(45, result.stats[0].baseStat)
    assertEquals(0, result.stats[0].effort)
    assertEquals("hp", result.stats[0].stat.name)
  }

  @Test
  fun `toEntity should handle null sprites other correctly`() {
    // Given
    val pokemon = createSamplePokemon().copy(
      sprites = PokemonSprites(
        frontDefault = "front.png",
        frontShiny = null,
        backDefault = null,
        other = null
      )
    )

    // When
    val result = pokemon.toEntity()

    // Then
    assertNotNull(result.sprites)
    assertEquals("front.png", result.sprites.frontDefault)
    assertNull(result.sprites.frontShiny)
    assertNull(result.sprites.backDefault)
    assertNull(result.sprites.other)
  }

  @Test
  fun `toEntity should convert PokemonListResponse to PokemonListEntity correctly`() {
    // Given
    val pokemonListResponse = PokemonListResponse(
      count = 1302,
      next = "https://pokeapi.co/api/v2/pokemon/?offset=20&limit=20",
      previous = null,
      results = listOf(
        PokemonListItem("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
        PokemonListItem("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/"),
        PokemonListItem("venusaur", "https://pokeapi.co/api/v2/pokemon/3/")
      )
    )

    // When
    val result = pokemonListResponse.toEntity()

    // Then
    assertNotNull(result)
    assertEquals(1302, result.count)
    assertEquals("https://pokeapi.co/api/v2/pokemon/?offset=20&limit=20", result.next)
    assertNull(result.previous)

    // Verify results conversion
    assertEquals(3, result.results.size)
    assertEquals("bulbasaur", result.results[0].name)
    assertEquals("https://pokeapi.co/api/v2/pokemon/1/", result.results[0].url)
    assertEquals("ivysaur", result.results[1].name)
    assertEquals("venusaur", result.results[2].name)
  }

  @Test
  fun `toEntity should handle empty list correctly`() {
    // Given
    val emptyResponse = PokemonListResponse(
      count = 0,
      next = null,
      previous = null,
      results = emptyList()
    )

    // When
    val result = emptyResponse.toEntity()

    // Then
    assertNotNull(result)
    assertEquals(0, result.count)
    assertNull(result.next)
    assertNull(result.previous)
    assertEquals(0, result.results.size)
  }

  @Test
  fun `toEntity should preserve ID extraction logic`() {
    // Given
    val pokemonListResponse = PokemonListResponse(
      count = 1,
      next = null,
      previous = null,
      results = listOf(
        PokemonListItem("pikachu", "https://pokeapi.co/api/v2/pokemon/25/")
      )
    )

    // When
    val result = pokemonListResponse.toEntity()

    // Then
    assertEquals(25, result.results[0].id)
  }

  private fun createSamplePokemon(): Pokemon {
    return Pokemon(
      id = 1,
      name = "bulbasaur",
      height = 7,
      weight = 69,
      baseExperience = 64,
      sprites = PokemonSprites(
        frontDefault = "front_default.png",
        frontShiny = "front_shiny.png",
        backDefault = "back_default.png",
        other = PokemonSpritesOther(
          officialArtwork = PokemonOfficialArtwork(
            frontDefault = "official_artwork.png"
          )
        )
      ),
      types = listOf(
        PokemonType(
          slot = 1,
          type = PokemonTypeInfo(
            name = "grass",
            url = "https://pokeapi.co/api/v2/type/12/"
          )
        ),
        PokemonType(
          slot = 2,
          type = PokemonTypeInfo(
            name = "poison",
            url = "https://pokeapi.co/api/v2/type/4/"
          )
        )
      ),
      stats = listOf(
        PokemonStat(
          baseStat = 45,
          effort = 0,
          stat = PokemonStatInfo(
            name = "hp",
            url = "https://pokeapi.co/api/v2/stat/1/"
          )
        ),
        PokemonStat(
          baseStat = 49,
          effort = 0,
          stat = PokemonStatInfo(
            name = "attack",
            url = "https://pokeapi.co/api/v2/stat/2/"
          )
        )
      )
    )
  }
}
