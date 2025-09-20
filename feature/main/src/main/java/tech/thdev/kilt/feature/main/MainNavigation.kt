package tech.thdev.kilt.feature.main

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * Navigation route for the main Pokemon list screen
 */
const val MainRoute = "main"

/**
 * Adds the main screen to the navigation graph
 */
fun NavGraphBuilder.mainScreen(
  modifier: Modifier = Modifier
) {
  composable(route = MainRoute) {
    MainScreen(modifier = modifier)
  }
}
