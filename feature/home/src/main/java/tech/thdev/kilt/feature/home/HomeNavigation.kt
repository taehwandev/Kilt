package tech.thdev.kilt.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import tech.thdev.kilt.feature.main.MainRoute
import tech.thdev.kilt.feature.main.mainScreen

/**
 * Main navigation composable for the home feature
 */
@Composable
fun HomeNavigation(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  startDestination: String = MainRoute
) {
  NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = modifier
  ) {
    // Pokemon list screen
    mainScreen()
  }
}
