# Kilt Generate Dagger KSP

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](../../LICENSE)
[![KSP](https://img.shields.io/badge/KSP-1.9.0-green.svg)](https://github.com/google/ksp)

A Kotlin Symbol Processing (KSP) library that automatically generates standard Dagger modules for dependency injection binding. This processor analyzes classes
annotated with `@KiltGenerateModule` and creates corresponding Dagger modules that bind implementation classes to their interfaces.

## Features

✨ **Automatic Module Generation** - No more manual Dagger module boilerplate  
🎯 **Scope-Aware** - Automatically detects and applies `@Singleton` and `@Reusable` scopes  
🔍 **Interface Detection** - Automatically discovers implemented interfaces  
🚀 **Pure Dagger** - Generates standard Dagger modules without Hilt dependencies  
📦 **Type-Safe** - Uses KotlinPoet for reliable code generation

## Installation

Add the processor to your module's `build.gradle.kts`:

```kotlin
dependencies {
    // Add the annotation
    implementation("tech.thdev.kilt:kilt-generate-annotations")
    
    // Add the KSP processor
    ksp("tech.thdev.kilt:kilt-generate-dagger-ksp")
    
    // Required Dagger dependencies
    implementation("com.google.dagger:dagger:2.57.1")
    ksp("com.google.dagger:dagger-compiler:2.57.1")
}
```

Make sure KSP is applied in your module:

```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.2.20-2.0.2"
}
```

## Usage

### Basic Usage

Simply annotate your implementation classes with `@KiltGenerateModule`:

```kotlin
interface UserRepository {
    fun getUsers(): List<User>
}

@KiltGenerateModule
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
    override fun getUsers(): List<User> = apiService.fetchUsers()
}
```

The processor will automatically generate:

```kotlin
// Generated: com.example.dagger.UserRepositoryImplModule
@Module
interface UserRepositoryImplModule {
    @Binds
    abstract fun bindUserRepository(userRepository: UserRepositoryImpl): UserRepository
}
```

### With Scoped Dependencies

#### Singleton Scope

```kotlin
@Singleton
@KiltGenerateModule
class UserRepositoryImpl @Inject constructor(
    private val database: UserDatabase
) : UserRepository {
    // implementation
}
```

**Generated:**

```kotlin
@Module
interface UserRepositoryImplModule {
    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepository: UserRepositoryImpl): UserRepository
}
```

#### Reusable Scope

```kotlin
@Reusable
@KiltGenerateModule
class NetworkRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : NetworkRepository {
    // implementation
}
```

**Generated:**

```kotlin
@Module
interface NetworkRepositoryImplModule {
    @Reusable
    @Binds
    abstract fun bindNetworkRepository(networkRepository: NetworkRepositoryImpl): NetworkRepository
}
```

### Multiple Interfaces

Works seamlessly with classes that implement multiple interfaces:

```kotlin
interface DataRepository {
    fun getData(): Data
}

interface CacheRepository {
    fun cache(data: Data)
}

@Singleton
@KiltGenerateModule
class LocalDataRepositoryImpl @Inject constructor() : DataRepository, CacheRepository {
  override fun getData(): Data {
    // implementation details
    return Data()
  }

  override fun cache(data: Data) {
    // implementation details
  }
}
```

**Generated:**

```kotlin
@Module
interface LocalDataRepositoryImplModule {
    @Singleton
    @Binds
    abstract fun bindDataRepository(localDataRepository: LocalDataRepositoryImpl): DataRepository
    
    @Singleton
    @Binds
    abstract fun bindCacheRepository(localDataRepository: LocalDataRepositoryImpl): CacheRepository
}
```

## Generated Code Structure

### Naming Conventions

| Input | Generated Output |
|-------|-----------------|
| **Package** | `com.example` → `com.example.dagger` |
| **Module Name** | `UserRepositoryImpl` → `UserRepositoryImplModule` |
| **Bind Method** | `UserRepository` → `bindUserRepository` |
| **Parameter Name** | `UserRepositoryImpl` → `userRepository` |

### File Organization

```
src/main/java/com/example/
├── UserRepository.kt
├── UserRepositoryImpl.kt                    # Your implementation
└── dagger/
    └── UserRepositoryImplModule.kt          # Generated module
```

## Supported Scopes

| Scope | Annotation | Description |
|-------|------------|-------------|
| **Unscoped** | None | Standard binding without scope |
| **Singleton** | `@Singleton` | Application-wide singleton |
| **Reusable** | `@Reusable` | Reusable instance (Dagger optimization) |

## Configuration

### KSP Arguments

You can pass arguments to the processor:

```kotlin
ksp {
    arg("isTest", "Y") // Enable test mode (skips validation)
}
```

### Gradle Configuration

```kotlin
// build.gradle.kts
android {
    sourceSets {
        getByName("main") {
            java.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}
```

## Requirements

- **Kotlin** 2.2.20+
- **KSP** 2.2.20-2.0.2+
- **Dagger** 2.57.1+

## Comparison with Manual Approach

### Before (Manual)

```kotlin
// UserRepositoryImpl.kt
@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {
    // implementation
}

// UserRepositoryModule.kt (manually created)
@Module
interface UserRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
```

### After (With Kilt)

```kotlin
// UserRepositoryImpl.kt
@Singleton
@KiltGenerateModule  // ← Just add this annotation!
class UserRepositoryImpl @Inject constructor() : UserRepository {
    // implementation
}

// UserRepositoryImplModule.kt is automatically generated!
```

## Troubleshooting

### Common Issues

**1. Module not generated**

- Ensure `@KiltGenerateModule` is applied to classes that implement interfaces
- Verify KSP is properly configured
- Check that the class is not abstract

**2. Build errors**

- Clean and rebuild: `./gradlew clean build`
- Check generated sources: `build/generated/ksp/main/kotlin`

**3. Scope conflicts**

- Only one scope annotation per class is supported
- Priority: `@Singleton` > `@Reusable` > no scope

### Debug Mode

Enable KSP logging to see processor output:

```kotlin
ksp {
    arg("verbose", "true")
}
```

## Architecture

### Core Components

```
KiltDaggerFactoryProcessorProvider
└── KiltDaggerFactoryProcessor
    ├── DaggerAnnotationVisitor      # Analyzes annotated classes
    ├── DaggerRepositoryModel        # Data model for generation
    └── DaggerModuleGenerator        # Code generation engine
```

### Processing Flow

1. **Discovery** - Find `@KiltGenerateModule` annotated classes
2. **Analysis** - Extract interfaces and scope annotations
3. **Validation** - Ensure classes are suitable for binding
4. **Generation** - Create Dagger modules with `@Binds` methods
5. **Output** - Write generated modules to build directory

## Contributing

Contributions are welcome! Please check the [contributing guidelines](../../CONTRIBUTING.md).

## License

This project is licensed under the MIT License - see the [LICENSE](../../LICENSE) file for details.

---

**Related Modules:**

- [`kilt-generate-annotations`](../kilt-generate-annotations) - Shared annotations
- [`kilt-generate-hilt-ksp`](../kilt-generate-hilt-ksp) - Hilt version of this processor