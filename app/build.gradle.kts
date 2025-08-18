plugins {
    alias(libs.plugins.tech.thdev.android.application)
    alias(libs.plugins.tech.thdev.android.library.hilt)
}

android {
    val (majorVersion, minorVersion, patchVersion, code) = getVersionInfo()

    namespace = "tech.thdev.kilt"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "tech.thdev.kilt"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = code
        versionName = "$majorVersion.$minorVersion.$patchVersion"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

ksp {
    arg("moduleName", project.name)
    arg("rootDir", rootDir.absolutePath)
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.androidx.core)

    implementation(libs.androidx.compose.activity)

    rootProject.subprojects.filterProject {
        implementation(it)
    }
}