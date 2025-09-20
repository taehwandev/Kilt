# Kilt Generate Hilt KSP

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](../../LICENSE)
[![KSP](https://img.shields.io/badge/KSP-1.9.0-green.svg)](https://github.com/google/ksp)
[![Hilt](https://img.shields.io/badge/Hilt-2.48-orange.svg)](https://dagger.dev/hilt/)

A Kotlin Symbol Processing (KSP) library that automatically generates Dagger Hilt modules for dependency injection binding. This processor analyzes classes
annotated with `@KiltGenerateModule` and creates corresponding Hilt modules that bind implementation classes to their interfaces with the appropriate Hilt
components.

## Features

✨ **Automatic Hilt Module Generation** - No more manual Hilt module boilerplate  
🎯 **Scope-Aware** - Automatically detects and applies Hilt scopes (`@Singleton`, `@ActivityRetainedScoped`, `@ViewModelScoped`)  
🔍 **Interface Detection** - Automatically discovers implemented interfaces  
🏗️ **Component Installation** - Automatically installs modules in the correct Hilt components  
📦 **Type-Safe** - Uses KotlinPoet for reliable code generation

## Installation

Add the processor to your module's `build.gradle.kts`:

```kotlin
dependencies {
    // Add the annotation
    implementation("tech.thdev.kilt:kilt-generate-annotations")
    
    // Add the KSP processor
    ksp("tech.thdev.kilt:kilt-generate-hilt-ksp")
    
    // Required Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
}
```

Make sure KSP and Hilt plugins are applied:

```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.2.20-2.0.2"
    id("com.google.dagger.hilt.android") version "2.57.1"
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
// Generated: com.example.hilt.UserRepositoryImplModule
@Module
@InstallIn(SingletonComponent::class)
interface UserRepositoryImplModule {
    @Binds
    abstract fun bindUserRepository(userRepository: UserRepositoryImpl): UserRepository
}
```

### With Hilt Scopes

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
@InstallIn(SingletonComponent::class)
interface UserRepositoryImplModule {
    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepository: UserRepositoryImpl): UserRepository
}
```

#### ActivityRetained Scope

```kotlin
@ActivityRetainedScoped
@KiltGenerateModule
class SessionRepositoryImpl @Inject constructor(
    private val preferences: SharedPreferences
) : SessionRepository {
    // implementation
}
```

**Generated:**

```kotlin
@Module
@InstallIn(ActivityRetainedComponent::class)
interface SessionRepositoryImplModule {
    @ActivityRetainedScoped
    @Binds
    abstract fun bindSessionRepository(sessionRepository: SessionRepositoryImpl): SessionRepository
}
```

#### ViewModel Scope

```kotlin
@ViewModelScoped
@KiltGenerateModule
class UserUseCaseImpl @Inject constructor(
    private val repository: UserRepository
) : UserUseCase {
    // implementation
}
```

**Generated:**

```kotlin
@Module
@InstallIn(ViewModelComponent::class)
interface UserUseCaseImplModule {
    @ViewModelScoped
    @Binds
    abstract fun bindUserUseCase(userUseCase: UserUseCaseImpl): UserUseCase
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
    override fun getData(): Data = // implementation
    override fun cache(data: Data) = // implementation
}
```

**Generated:**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
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
| **Package** | `com.example` → `com.example.hilt` |
| **Module Name** | `UserRepositoryImpl` → `UserRepositoryImplModule` |
| **Bind Method** | `UserRepository` → `bindUserRepository` |
| **Parameter Name** | `UserRepositoryImpl` → `userRepository` |

### File Organization

```
src/main/java/com/example/
├── UserRepository.kt
├── UserRepositoryImpl.kt                    # Your implementation
└── hilt/
    └── UserRepositoryImplModule.kt          # Generated module
```

## Supported Scopes & Components

| Scope | Annotation | Hilt Component | Lifecycle |
|-------|------------|----------------|-----------|
| **Singleton** | `@Singleton` | `SingletonComponent` | Application lifetime |
| **ActivityRetained** | `@ActivityRetainedScoped` | `ActivityRetainedComponent` | Activity recreation |
| **ViewModel** | `@ViewModelScoped` | `ViewModelComponent` | ViewModel lifetime |
| **Unscoped** | None | `SingletonComponent` | No scope management |

### Component Selection Logic

The processor automatically selects the appropriate Hilt component based on scope:

```kotlin
when (scopeAnnotation) {
    @Singleton -> SingletonComponent::class
    @ActivityRetainedScoped -> ActivityRetainedComponent::class  
    @ViewModelScoped -> ViewModelComponent::class
    null -> SingletonComponent::class // Default
}
```

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

hilt {
    enableAggregatingTask = true
    enableExperimentalClasspathAggregation = true
}
```

## Requirements

- **Kotlin** 2.2.20+
- **KSP** 2.2.20-2.0.2+
- **Dagger Hilt** 2.57.1+
- **Android Gradle Plugin** 8.13.0+

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
@InstallIn(SingletonComponent::class)
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

// UserRepositoryImplModule.kt is automatically generated with correct component!
```

## Advanced Usage

### Custom Application Class

Your application class needs the `@HiltAndroidApp` annotation:

```kotlin
@HiltAndroidApp
class MyApplication : Application()
```

### Activity Integration

```kotlin
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    @Inject
    lateinit var userRepository: UserRepository // Automatically bound!
    
    // ...
}
```

### Fragment Integration

```kotlin
@AndroidEntryPoint
class UserFragment : Fragment() {
    
    @Inject
    lateinit var userRepository: UserRepository
    
    // ...
}
```

### ViewModel Integration

```kotlin
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userUseCase: UserUseCase // Automatically bound!
) : ViewModel() {
    // ...
}
```

## Troubleshooting

### Common Issues

**1. Module not generated**

- Ensure `@KiltGenerateModule` is applied to classes that implement interfaces
- Verify KSP and Hilt plugins are properly configured
- Check that the class is not abstract

**2. Hilt component errors**

- Ensure your Application class has `@HiltAndroidApp`
- Verify Activities/Fragments have `@AndroidEntryPoint`
- Check scope compatibility with injection sites

**3. Build errors**

- Clean and rebuild: `./gradlew clean build`
- Check generated sources: `build/generated/ksp/main/kotlin`
- Verify Hilt annotation processor is working

**4. Scope mismatches**

- `@ViewModelScoped` can only be injected into `@HiltViewModel` ViewModels
- `@ActivityRetainedScoped` survives configuration changes
- `@Singleton` is available application-wide

### Debug Mode

Enable KSP and Hilt logging:

```kotlin
ksp {
    arg("verbose", "true")
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

## Architecture

### Core Components

```
KiltHiltFactoryProcessorProvider
└── KiltHiltFactoryProcessor
    ├── AnnotationVisitor           # Analyzes annotated classes
    ├── ResearchRepositoryModel     # Data model for generation
    └── DaggerModuleGenerator       # Hilt module generation engine
```

### Processing Flow

1. **Discovery** - Find `@KiltGenerateModule` annotated classes
2. **Analysis** - Extract interfaces and Hilt scope annotations
3. **Component Selection** - Determine appropriate Hilt component
4. **Validation** - Ensure classes are suitable for Hilt binding
5. **Generation** - Create Hilt modules with `@InstallIn` and `@Binds`
6. **Output** - Write generated modules to build directory

## Migration from Standard Dagger

If you're migrating from the standard Dagger processor:

1. **Update dependencies:**
   ```kotlin
   // Remove
   // ksp("tech.thdev.kilt:kilt-generate-dagger-ksp")
   
   // Add
   ksp("tech.thdev.kilt:kilt-generate-hilt-ksp")
   ```

2. **Update generated package imports:**
   ```kotlin
   // Change from
   // import com.example.dagger.UserRepositoryImplModule
   
   // To  
   // import com.example.hilt.UserRepositoryImplModule
   ```

3. **Remove manual component installation** - Hilt handles this automatically!

## Best Practices

### Scope Selection Guidelines

- **Use `@Singleton`** for app-wide dependencies (databases, network clients, repositories)
- **Use `@ActivityRetainedScoped`** for data that survives configuration changes but not process death
- **Use `@ViewModelScoped`** for use cases and data specific to ViewModels
- **Avoid unscoped** dependencies for heavy objects

### Performance Considerations

- Hilt modules are processed at compile time - no runtime overhead
- Generated code is optimized and type-safe
- Use appropriate scopes to avoid memory leaks

## Contributing

Contributions are welcome! Please check the [contributing guidelines](../../CONTRIBUTING.md).

## License

This project is licensed under the MIT License - see the [LICENSE](../../LICENSE) file for details.

---

**Related Modules:**

- [`kilt-generate-annotations`](../kilt-generate-annotations) - Shared annotations
- [`kilt-generate-dagger-ksp`](../kilt-generate-dagger-ksp) - Standard Dagger version of this processor