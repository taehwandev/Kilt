package tech.thdev.kilt.poke.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import tech.thdev.kilt.poke.repository.api.PokeApiService
import tech.thdev.kilt.poke.repository.model.Pokemon
import tech.thdev.kilt.poke.repository.model.PokemonListItem
import tech.thdev.kilt.poke.repository.model.PokemonListResponse
import tech.thdev.kilt.poke.repository.model.PokemonSprites
import tech.thdev.kilt.poke.repository.model.PokemonStat
import tech.thdev.kilt.poke.repository.model.PokemonStatInfo
import tech.thdev.kilt.poke.repository.model.PokemonType
import tech.thdev.kilt.poke.repository.model.PokemonTypeInfo

@RunWith(MockitoJUnitRunner::class)
class PokemonRepositoryImplTest {

  @Mock
  private lateinit var apiService: PokeApiService

  private lateinit var repository: PokemonRepositoryImpl

  @Before
  fun setUp() {
    repository = PokemonRepositoryImpl(apiService)
  }

  @Test
  fun `getPokemonList should return converted PokemonListEntity when API call succeeds`() = runTest {
    // Given
    val mockApiResponse = PokemonListResponse(
      count = 1302,
      next = "https://pokeapi.co/api/v2/pokemon/?offset=20&limit=20",
      previous = null,
      results = listOf(
        PokemonListItem("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
        PokemonListItem("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/")
      )
    )

    `when`(apiService.getPokemonList(20, 0)).thenReturn(mockApiResponse)

    // When
    val result = repository.getPokemonList(20, 0)

    // Then
    assertNotNull(result)
    assertEquals(1302, result.count)
    assertEquals("https://pokeapi.co/api/v2/pokemon/?offset=20&limit=20", result.next)
    assertEquals(null, result.previous)
    assertEquals(2, result.results.size)
    assertEquals("bulbasaur", result.results[0].name)
    assertEquals("ivysaur", result.results[1].name)

    verify(apiService).getPokemonList(20, 0)
  }

  @Test
  fun `getPokemonById should return converted PokemonEntity when API call succeeds`() = runTest {
    // Given
    val mockPokemon = createMockPokemon(1, "bulbasaur")
    `when`(apiService.getPokemonById(1)).thenReturn(mockPokemon)

    // When
    val result = repository.getPokemonById(1)

    // Then
    assertNotNull(result)
    assertEquals(1, result.id)
    assertEquals("bulbasaur", result.name)
    assertEquals(7, result.height)
    assertEquals(69, result.weight)
    assertEquals(64, result.baseExperience)
    assertEquals(1, result.types.size)
    assertEquals("grass", result.types[0].type.name)

    verify(apiService).getPokemonById(1)
  }

  @Test
  fun `getPokemonByName should return converted PokemonEntity with lowercase name`() = runTest {
    // Given
    val mockPokemon = createMockPokemon(1, "bulbasaur")
    `when`(apiService.getPokemon("bulbasaur")).thenReturn(mockPokemon)

    // When
    val result = repository.getPokemonByName("BULBASAUR")

    // Then
    assertNotNull(result)
    assertEquals(1, result.id)
    assertEquals("bulbasaur", result.name)

    verify(apiService).getPokemon("bulbasaur")
  }

  @Test
  fun `getPokemonByIds should return list of converted PokemonEntity`() = runTest {
    // Given
    val pokemon1 = createMockPokemon(1, "bulbasaur")
    val pokemon2 = createMockPokemon(2, "ivysaur")
    val pokemon3 = createMockPokemon(3, "venusaur")

    `when`(apiService.getPokemonById(1)).thenReturn(pokemon1)
    `when`(apiService.getPokemonById(2)).thenReturn(pokemon2)
    `when`(apiService.getPokemonById(3)).thenReturn(pokemon3)

    // When
    val result = repository.getPokemonByIds(listOf(1, 2, 3))

    // Then
    assertNotNull(result)
    assertEquals(3, result.size)
    assertEquals("bulbasaur", result[0].name)
    assertEquals("ivysaur", result[1].name)
    assertEquals("venusaur", result[2].name)

    verify(apiService).getPokemonById(1)
    verify(apiService).getPokemonById(2)
    verify(apiService).getPokemonById(3)
  }

  @Test
  fun `getPokemonList should handle empty results`() = runTest {
    // Given
    val emptyResponse = PokemonListResponse(
      count = 0,
      next = null,
      previous = null,
      results = emptyList()
    )

    `when`(apiService.getPokemonList(0, 0)).thenReturn(emptyResponse)

    // When
    val result = repository.getPokemonList(0, 0)

    // Then
    assertNotNull(result)
    assertEquals(0, result.count)
    assertEquals(0, result.results.size)

    verify(apiService).getPokemonList(0, 0)
  }

  @Test
  fun `getPokemonByIds should handle empty list`() = runTest {
    // When
    val result = repository.getPokemonByIds(emptyList())

    // Then
    assertNotNull(result)
    assertEquals(0, result.size)
  }

  private fun createMockPokemon(id: Int, name: String): Pokemon {
    return Pokemon(
      id = id,
      name = name,
      height = 7,
      weight = 69,
      baseExperience = 64,
      sprites = PokemonSprites(
        frontDefault = "https://example.com/front.png",
        frontShiny = "https://example.com/shiny.png",
        backDefault = "https://example.com/back.png",
        other = null
      ),
      types = listOf(
        PokemonType(
          slot = 1,
          type = PokemonTypeInfo(
            name = "grass",
            url = "https://pokeapi.co/api/v2/type/12/"
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
        )
      )
    )
  }
}
