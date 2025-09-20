package tech.thdev.kilt.feature.main

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import tech.thdev.kilt.poke.api.PokemonRepository
import tech.thdev.kilt.poke.api.model.PokemonListEntity

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

  @Mock
  private lateinit var pokemonRepository: PokemonRepository

  private lateinit var viewModel: MainViewModel
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `initial state should be correct`() = runTest {
    // Given
    val mockResponse = createMockPokemonListEntity()
    `when`(pokemonRepository.getPokemonList(20, 0)).thenReturn(mockResponse)

    // When
    viewModel = MainViewModel(pokemonRepository)
    advanceUntilIdle()

    // Then
    val finalState = viewModel.uiState.value
    assertFalse(finalState.isLoading)
    assertFalse(finalState.isRefreshing)
    assertEquals(3, finalState.pokemonList.size)
    assertNull(finalState.error)
    assertTrue(finalState.hasMore)
  }

  @Test
  fun `loadPokemon should update state correctly on success`() = runTest {
    // Given
    val mockResponse = createMockPokemonListEntity()
    `when`(pokemonRepository.getPokemonList(20, 0)).thenReturn(mockResponse)

    viewModel = MainViewModel(pokemonRepository)

    // When & Then
    viewModel.uiState.test {
      // Skip initial state
      skipItems(1)

      // Should emit loading state
      val loadingState = awaitItem()
      assertTrue(loadingState.isLoading)
      assertNull(loadingState.error)

      // Should emit success state
      val successState = awaitItem()
      assertFalse(successState.isLoading)
      assertEquals(3, successState.pokemonList.size)
      assertNull(successState.error)
      assertTrue(successState.hasMore)
    }

    advanceUntilIdle()
  }

  @Test
  fun `loadPokemon should handle error correctly`() = runTest {
    // Given
    val errorMessage = "Network error"
    `when`(pokemonRepository.getPokemonList(20, 0)).thenThrow(RuntimeException(errorMessage))

    viewModel = MainViewModel(pokemonRepository)

    viewModel.uiState.test {
      // Skip initial state
      skipItems(1)

      // Should emit loading state
      val loadingState = awaitItem()
      assertTrue(loadingState.isLoading)
      assertNull(loadingState.error)

      // Should emit error state
      val errorState = awaitItem()
      assertFalse(errorState.isLoading)
      assertEquals(errorMessage, errorState.error)
      assertTrue(errorState.pokemonList.isEmpty())
    }

    advanceUntilIdle()
  }

  @Test
  fun `refresh should reset data and reload`() = runTest {
    // Given
    val initialResponse = createMockPokemonListEntity()
    val refreshResponse = createMockPokemonListEntity(
      offset = 0,
      pokemonNames = listOf("refreshed-pokemon1", "refreshed-pokemon2")
    )

    `when`(pokemonRepository.getPokemonList(20, 0))
      .thenReturn(initialResponse)
      .thenReturn(refreshResponse)

    viewModel = MainViewModel(pokemonRepository)
    advanceUntilIdle()

    // When
    viewModel.uiState.test {
      // Skip initial states
      skipItems(2)

      viewModel.refresh()

      // Should emit refreshing state
      val refreshingState = awaitItem()
      assertTrue(refreshingState.isRefreshing)
      assertFalse(refreshingState.isLoading)
      assertNull(refreshingState.error)

      // Should emit refreshed data
      val refreshedState = awaitItem()
      assertFalse(refreshedState.isRefreshing)
      assertFalse(refreshedState.isLoading)
      assertEquals(2, refreshedState.pokemonList.size)
      assertEquals("Refreshed-pokemon1", refreshedState.pokemonList[0].displayName)
    }

    advanceUntilIdle()
  }

  @Test
  fun `retry should reload data after error`() = runTest {
    // Given
    val errorMessage = "Network error"
    val successResponse = createMockPokemonListEntity()

    `when`(pokemonRepository.getPokemonList(20, 0))
      .thenThrow(RuntimeException(errorMessage))
      .thenReturn(successResponse)

    viewModel = MainViewModel(pokemonRepository)
    advanceUntilIdle()

    // When
    viewModel.uiState.test {
      // Skip to error state
      skipItems(2)

      viewModel.retry()

      // Should emit loading state
      val loadingState = awaitItem()
      assertTrue(loadingState.isLoading)
      assertNull(loadingState.error)

      // Should emit success state
      val successState = awaitItem()
      assertFalse(successState.isLoading)
      assertEquals(3, successState.pokemonList.size)
      assertNull(successState.error)
    }

    advanceUntilIdle()
  }

  @Test
  fun `clearError should remove error from state`() = runTest {
    // Given
    val errorMessage = "Network error"
    `when`(pokemonRepository.getPokemonList(20, 0)).thenThrow(RuntimeException(errorMessage))

    viewModel = MainViewModel(pokemonRepository)
    advanceUntilIdle()

    // When
    viewModel.clearError()

    // Then
    val finalState = viewModel.uiState.value
    assertNull(finalState.error)
  }

  private fun createMockPokemonListEntity(
    offset: Int = 0,
    pokemonNames: List<String> = listOf("bulbasaur", "ivysaur", "venusaur"),
    hasNext: Boolean = true
  ): PokemonListEntity {
    val results = pokemonNames.mapIndexed { index, name ->
      PokemonListEntity.PokemonListItem(
        name = name,
        url = "https://pokeapi.co/api/v2/pokemon/${offset + index + 1}/"
      )
    }

    return PokemonListEntity(
      count = 1302,
      next = if (hasNext) "https://pokeapi.co/api/v2/pokemon/?offset=${offset + 20}&limit=20" else null,
      previous = if (offset > 0) "https://pokeapi.co/api/v2/pokemon/?offset=${offset - 20}&limit=20" else null,
      results = results
    )
  }
}
