import tech.thdev.gradle.configureBuildConfig

plugins {
    alias(libs.plugins.tech.thdev.android.library)
    alias(libs.plugins.tech.thdev.kotlin.library.hilt)
    alias(libs.plugins.tech.thdev.kotlin.library.serialization)
}

setNamespace("network")
configureBuildConfig()

dependencies {
    implementation(projects.coreApp.network.networkApi)

    implementation(libs.network.retrofit)
    implementation(libs.network.retrofit.kotlinxSerializationConvert)
    implementation(libs.network.okhttp)
    implementation(libs.network.okhttp.logging)
}
