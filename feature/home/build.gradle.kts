plugins {
  alias(libs.plugins.tech.thdev.android.library.feature.compose)
  alias(libs.plugins.tech.thdev.android.library.navigation)
}

setNamespace("feature.home")

dependencies {
  // Feature modules
  implementation(projects.feature.main)

  // UI and Navigation dependencies
  implementation(libs.androidx.compose.activity)
}
