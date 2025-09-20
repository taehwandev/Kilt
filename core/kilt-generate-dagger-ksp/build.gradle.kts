plugins {
    alias(libs.plugins.tech.thdev.android.library)
}

setNamespace("kilt.generate.dagger.ksp")

dependencies {
    implementation(libs.ksp)
    implementation(libs.ksp.kotlinPoet)

    implementation(projects.core.kiltGenerateAnnotations)

    testImplementation(libs.test.kotlinCompilTesting)
    testImplementation(libs.test.kotlinCompilTestingKSP)
}
