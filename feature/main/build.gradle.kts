plugins {
  alias(libs.plugins.tech.thdev.android.library.feature.compose)
}

setNamespace("feature.main")

dependencies {
  // Core dependencies
  implementation(projects.coreApp.data.pokeRepositoryApi)

  // UI dependencies
  implementation(libs.androidx.compose.lifecycle.viewModel)
  implementation(libs.androidx.compose.navigation.hilt)

  // Image loading
  implementation(libs.image.coil)
}
