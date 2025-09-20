plugins {
  alias(libs.plugins.tech.thdev.kotlin.library)
  alias(libs.plugins.tech.thdev.android.library.publish)
}

val (majorVersion, minorVersion, patchVersion, code) = getVersionInfo()
version = "$majorVersion.$minorVersion.$patchVersion"

// Module-specific publication configuration
extra["publication.name"] = "Kilt Generate Annotations"
extra["publication.description"] = "Core annotations for Kilt automatic Dagger/Hilt module generation"
extra["publication.artifactId"] = "kilt-generate-annotations"
