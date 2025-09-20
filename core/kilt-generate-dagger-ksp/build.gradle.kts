plugins {
    alias(libs.plugins.tech.thdev.kotlin.library)
}

dependencies {
    implementation(libs.ksp)
    implementation(libs.ksp.kotlinPoet)

    implementation(projects.core.kiltGenerateAnnotations)

    testImplementation(libs.test.kotlinCompilTesting)
    testImplementation(libs.test.kotlinCompilTestingKSP)
}
