package tech.thdev.kilt.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * Main Pokemon list screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  viewModel: MainViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val pullToRefreshState = rememberPullToRefreshState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Pokédex",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
      )
    },
    modifier = modifier
  ) { paddingValues ->
    PullToRefreshBox(
      state = pullToRefreshState,
      isRefreshing = uiState.isRefreshing,
      onRefresh = viewModel::refresh,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when {
        uiState.isInitialLoading() -> {
          InitialLoadingContent()
        }

        uiState.hasErrorWithEmptyContent() -> {
          ErrorContent(
            error = uiState.error ?: "Unknown error",
            onRetry = viewModel::retry
          )
        }

        else -> {
          PokemonListContent(
            uiState = uiState,
          )
        }
      }
    }
  }
}

@Composable
private fun InitialLoadingContent(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      CircularProgressIndicator(
        modifier = Modifier.size(48.dp),
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Loading Pokémon...",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
private fun ErrorContent(
  error: String,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(24.dp)
    ) {
      Text(
        text = "Oops! Something went wrong",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = error,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(24.dp))
      Button(
        onClick = onRetry,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Try Again")
      }
    }
  }
}

@Composable
private fun PokemonListContent(
  uiState: MainUiState,
  modifier: Modifier = Modifier
) {
  val listState = rememberLazyListState()

  LazyColumn(
    state = listState,
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = modifier.fillMaxSize()
  ) {
    items(
      items = uiState.pokemonList,
      key = { it.id }
    ) { pokemon ->
      PokemonCard(pokemon = pokemon)
    }

    if (uiState.isLoading && uiState.pokemonList.isNotEmpty()) {
      item {
        LoadingMoreContent()
      }
    }

    if (!uiState.hasMore && uiState.pokemonList.isNotEmpty()) {
      item {
        Text(
          text = "You've seen all Pokémon! 🎉",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
        )
      }
    }
  }
}

@Composable
private fun PokemonCard(
  pokemon: PokemonItemUiState,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Pokemon Image
      AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(pokemon.imageUrl)
          .crossfade(true)
          .build(),
        contentDescription = "Pokemon ${pokemon.displayName}",
        modifier = Modifier.size(80.dp),
        contentScale = ContentScale.Fit
      )

      Spacer(modifier = Modifier.width(16.dp))

      // Pokemon Info
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = pokemon.displayName,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "#${pokemon.id.toString().padStart(3, '0')}",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
private fun LoadingMoreContent(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        strokeWidth = 2.dp,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.width(12.dp))
      Text(
        text = "Loading more Pokémon...",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
  MaterialTheme {
    // Preview with sample data would go here
  }
}
