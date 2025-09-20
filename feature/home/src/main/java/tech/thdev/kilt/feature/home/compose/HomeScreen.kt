package tech.thdev.kilt.feature.home.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import tech.thdev.kilt.feature.home.HomeNavigation

/**
 * Main composable for the Home screen with navigation
 */
@Composable
internal fun HomeScreen() {
  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    HomeNavigation(
      modifier = Modifier.fillMaxSize()
    )
  }
}

/**
 * Material 3 theme for the Kilt application
 */
@Composable
internal fun KiltTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = MaterialTheme.colorScheme,
    typography = MaterialTheme.typography,
    content = content
  )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
  KiltTheme {
    HomeScreen()
  }
}
