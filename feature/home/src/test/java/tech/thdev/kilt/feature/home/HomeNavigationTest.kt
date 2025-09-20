package tech.thdev.kilt.feature.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeNavigationTest {

  @Test
  fun `navigation routes should have correct values`() {
    assertEquals("home", HomeNavigationRoutes.HOME)
    assertEquals("pokemon_detail", HomeNavigationRoutes.POKEMON_DETAIL)
    assertEquals("search", HomeNavigationRoutes.SEARCH)
    assertEquals("favorites", HomeNavigationRoutes.FAVORITES)
  }
}