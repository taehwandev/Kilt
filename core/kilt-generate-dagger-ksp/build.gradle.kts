plugins {
    alias(libs.plugins.tech.thdev.kotlin.library)
    alias(libs.plugins.tech.thdev.android.library.publish)
}

val (majorVersion, minorVersion, patchVersion, code) = getVersionInfo()
version = "$majorVersion.$minorVersion.$patchVersion"

// Module-specific publication configuration
extra["publication.name"] = "Kilt Generate Dagger KSP"
extra["publication.description"] = "KSP processor for generating standard Dagger modules automatically"
extra["publication.artifactId"] = "kilt-generate-dagger-ksp"

dependencies {
    implementation(libs.ksp)
    implementation(libs.ksp.kotlinPoet)

    implementation(projects.core.kiltGenerateAnnotations)

    testImplementation(libs.test.kotlinCompilTesting)
    testImplementation(libs.test.kotlinCompilTestingKSP)
}
