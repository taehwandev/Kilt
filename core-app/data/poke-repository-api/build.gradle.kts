plugins {
  alias(libs.plugins.tech.thdev.android.library)
  alias(libs.plugins.tech.thdev.kotlin.library.hilt)
  alias(libs.plugins.tech.thdev.kotlin.library.serialization)
}

setNamespace("poke.repository.api")

dependencies {
  implementation(projects.coreApp.data.pokeRepositoryApi)

  // Use Generate ksp
  implementation(projects.core.kiltGenerateAnnotations)
  ksp(projects.core.kiltGenerateHiltKsp)

  implementation(projects.coreApp.network.networkApi)

  implementation(libs.network.retrofit)
}
