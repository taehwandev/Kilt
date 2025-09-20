pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Kilt"
include(":app")

// library
include(
    ":core:kilt-generate-annotations",
    ":core:kilt-generate-dagger-ksp",
    ":core:kilt-generate-hilt-ksp",
)

// core-app modules
include(
    ":core-app:data:poke-repository",
    ":core-app:data:poke-repository-api",
    ":core-app:network:network",
    ":core-app:network:network-api",
)
