package tech.thdev.kilt.feature.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import tech.thdev.kilt.feature.home.compose.HomeScreen
import tech.thdev.kilt.feature.home.compose.KiltTheme

/**
 * Home Activity that serves as the main entry point for the application.
 *
 * This activity handles:
 * - Navigation between different features
 * - Material 3 theming
 * - Edge-to-edge display
 * - Hilt dependency injection
 */
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      KiltTheme {
        HomeScreen()
      }
    }
  }
}
