package tech.thdev.kilt.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.thdev.kilt.poke.api.PokemonRepository
import javax.inject.Inject

/**
 * ViewModel for the main Pokemon list screen
 *
 * This ViewModel manages the Pokemon list state and handles user interactions
 * such as loading Pokemon, refreshing the list, and pagination.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
  private val pokemonRepository: PokemonRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(MainUiState())
  val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

  private var currentOffset = 0
  private val pageSize = 20

  init {
    loadPokemon()
  }

  /**
   * Load Pokemon list (initial load or pagination)
   */
  fun loadPokemon(isRefresh: Boolean = false) {
    if (isRefresh) {
      currentOffset = 0
      _uiState.update {
        it.copy(
          isRefreshing = true,
          error = null,
        )
      }
    } else if (_uiState.value.isLoading) {
      return // Prevent multiple simultaneous loads
    } else {
      _uiState.value = _uiState.value.copy(
        isLoading = true,
        error = null
      )
    }

    viewModelScope.launch {
      try {
        val pokemonListResponse = pokemonRepository.getPokemonList(
          limit = pageSize,
          offset = currentOffset
        )

        val newPokemonList = pokemonListResponse.toUiState()
        val currentList = if (isRefresh) emptyList() else _uiState.value.pokemonList

        _uiState.update {
          it.copy(
            isLoading = false,
            isRefreshing = false,
            pokemonList = currentList + newPokemonList,
            error = null,
            hasMore = pokemonListResponse.next != null,
            nextUrl = pokemonListResponse.next,
          )
        }

        currentOffset += pageSize

      } catch (exception: Exception) {
        _uiState.update {
          it.copy(
            isLoading = false,
            isRefreshing = false,
            error = exception.message ?: "Unknown error occurred",
          )
        }
      }
    }
  }

  /**
   * Refresh the Pokemon list
   */
  fun refresh() {
    loadPokemon(isRefresh = true)
  }

  /**
   * Retry loading Pokemon after an error
   */
  fun retry() {
    loadPokemon(isRefresh = false)
  }

  /**
   * Clear any error state
   */
  fun clearError() {
    _uiState.update {
      it.copy(
        error = null,
      )
    }
  }
}
