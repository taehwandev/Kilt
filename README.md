# Kilt (Kotlin + Hilt)

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-purple.svg)](https://kotlinlang.org)
[![KSP](https://img.shields.io/badge/KSP-2.2.20--2.0.2-green.svg)](https://github.com/google/ksp)
[![Hilt](https://img.shields.io/badge/Hilt-2.57.1-orange.svg)](https://dagger.dev/hilt/)

**Automatic Dagger & Hilt module generation using Kotlin Symbol Processing (KSP)**

Kilt eliminates boilerplate code by automatically generating Dagger and Hilt modules for your dependency injection setup. Simply add `@KiltGenerateModule` to
your implementation classes, and Kilt will handle the rest!

## What is Kilt?

Kilt is a KSP (Kotlin Symbol Processing) library that automatically generates dependency injection modules for both **standard Dagger** and **Hilt**. It reduces
boilerplate code and ensures consistent module generation patterns across your codebase.

### Key Features

- **Automatic Module Generation** - No more manual Dagger/Hilt modules
- **Scope-Aware** - Detects `@Singleton`, `@Reusable`, `@ActivityRetainedScoped`, `@ViewModelScoped`
- **Component Installation** - Automatic Hilt component installation
- **Type-Safe** - Uses KotlinPoet for reliable code generation
- **Well-Tested** - Comprehensive test coverage
- **Well-Documented** - Extensive documentation and examples

## Demo Application

This repository includes a complete **Pokemon Explorer** app demonstrating Kilt's capabilities:

- **Pokemon List** with infinite scrolling
- **Pull-to-refresh** functionality
- **Material 3 Design** with modern UI/UX
- **Repository Pattern** with automatic Hilt injection
- **MVVM Architecture** using Compose

## Project Structure

```
Kilt/
├── core/                           # Core KSP processors
│   ├── kilt-generate-annotations/     # @KiltGenerateModule annotation
│   ├── kilt-generate-dagger-ksp/     # Standard Dagger processor
│   └── kilt-generate-hilt-ksp/       # Hilt processor
├── core-app/                      # Application core modules
│   ├── data/poke-repository-api/     # Repository interface
│   ├── data/poke-repository/         # Repository implementation
│   └── network/                      # Network layer
├── feature/                       # Feature modules
│   ├── home/                         # Main navigation & app entry
│   └── main/                         # Pokemon list screen
├── build-logic/                   # Build configuration
├── config/                        # Code quality configs
└── app/                           # Main application module
```

## Quick Start

### 1. Add Dependencies

```kotlin
dependencies {
    // Core annotation
    implementation("tech.thdev.kilt:kilt-generate-annotations")
    
    // Choose your processor:
    // For Hilt projects:
    ksp("tech.thdev.kilt:kilt-generate-hilt-ksp")
    
    // For standard Dagger projects:
    ksp("tech.thdev.kilt:kilt-generate-dagger-ksp")
}
```

### 2. Apply KSP Plugin

```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.2.20-2.0.2"
}
```

### 3. Annotate Your Classes

```kotlin
interface UserRepository {
    fun getUsers(): List<User>
}

@Singleton
@KiltGenerateModule
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
    override fun getUsers() = apiService.fetchUsers()
}
```

### 4. Kilt Generates the Module Automatically!

**For Hilt:**

```kotlin
// Generated: UserRepositoryImplModule.kt
@Module
@InstallIn(SingletonComponent::class)
interface UserRepositoryImplModule {
    @Singleton
    @Binds
    abstract fun bindUserRepository(
        userRepository: UserRepositoryImpl
    ): UserRepository
}
```

**For Dagger:**

```kotlin
// Generated: UserRepositoryImplModule.kt  
@Module
interface UserRepositoryImplModule {
    @Singleton
    @Binds
    abstract fun bindUserRepository(
        userRepository: UserRepositoryImpl
    ): UserRepository
}
```

## Core Modules

### kilt-generate-annotations

The core annotation library containing `@KiltGenerateModule`.

### kilt-generate-hilt-ksp

KSP processor for **Hilt** projects. Features:

- Automatic `@InstallIn` with correct components
- Supports `@Singleton`, `@ActivityRetainedScoped`, `@ViewModelScoped`
- Hilt-specific optimizations

### kilt-generate-dagger-ksp

KSP processor for **standard Dagger** projects. Features:

- Pure Dagger modules without Hilt dependencies
- Supports `@Singleton` and `@Reusable` scopes
- Lightweight and focused

## Demo Features

### feature/home - App Entry Point

- Navigation management with Jetpack Navigation Compose
- Material 3 theming and edge-to-edge display
- Hilt integration with `@HiltAndroidApp`

### feature/main - Pokemon List

- Modern Compose UI with infinite scrolling
- Pull-to-refresh functionality
- MVVM architecture with `@HiltViewModel`
- Image loading with Coil
- Comprehensive error handling

### core-app/data - Data Layer

- Clean architecture with Repository pattern
- **Automatic Hilt module generation** using Kilt
- Pokemon API integration with Retrofit
- Comprehensive unit tests

## Supported Scopes & Components

### Hilt Processor

| Scope                     | Component                   | Lifecycle           |
|---------------------------|-----------------------------|---------------------|
| `@Singleton`              | `SingletonComponent`        | Application         |
| `@ActivityRetainedScoped` | `ActivityRetainedComponent` | Activity Recreation |
| `@ViewModelScoped`        | `ViewModelComponent`        | ViewModel           |
| No scope                  | `SingletonComponent`        | Default             |

### Dagger Processor

| Scope        | Description                    |
|--------------|--------------------------------|
| `@Singleton` | Application-wide singleton     |
| `@Reusable`  | Reusable instance optimization |
| No scope     | Standard binding               |

## Build System

### Convention Plugins

Custom Gradle convention plugins in `build-logic/`:

- `tech.thdev.android.library`
- `tech.thdev.android.library.feature.compose`
- `tech.thdev.kotlin.library`
- Type-safe project accessors

### Version Catalog

Centralized dependency management with `gradle/libs.versions.toml`:

- Latest Kotlin (2.2.20) and KSP
- Jetpack Compose with Material 3
- Hilt and Dagger dependencies
- Testing frameworks

## Testing Strategy

### Unit Tests

- **MockK** and **Mockito** for mocking
- **Turbine** for Flow testing
- **Kotlin Compile Testing** for KSP processors
- Comprehensive test coverage across all modules

### Integration Tests

- Repository integration tests
- ViewModel state management tests
- UI component tests

## Documentation

Each module includes detailed README files:

- **Architecture decisions** and patterns
- **Usage examples** with code snippets
- **API documentation**
- **Testing guidelines**
- **Future enhancement** roadmaps

## Getting Started with the Demo

### Prerequisites

- Android Studio Hedgehog+ (recommended)
- JDK 17+
- Android SDK 36+

### Run the Pokemon Demo

```bash
git clone https://github.com/your-username/Kilt.git
cd Kilt
./gradlew :feature:home:assembleDebug
```

### Explore Generated Code

After building, check:

```
build/generated/ksp/main/kotlin/
```

## Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md).

### Development Setup

```bash
# Clone the repository
git clone https://github.com/your-username/Kilt.git

# Build all modules
./gradlew build

# Run tests
./gradlew test

# Run code quality checks
./gradlew detekt
```

## Requirements

- **Kotlin** 2.2.20+
- **KSP** 2.2.20-2.0.2+
- **Dagger/Hilt** 2.57.1+
- **Android Gradle Plugin** 8.13.0+

## Roadmap

### Current (v25.9.0)

- Basic Dagger/Hilt module generation
- Scope detection and handling
- Component installation
- Comprehensive testing

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 TaeHwan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## Support

- **Documentation**: [Module READMEs](core/)
- **Issues**: [GitHub Issues](https://github.com/your-username/Kilt/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-username/Kilt/discussions)

## Show Your Support

If you find Kilt useful, please consider giving it a star on GitHub!

---

**Made with by [TaeHwan](https://github.com/your-username)**
