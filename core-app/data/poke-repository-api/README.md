# Pokemon Repository (Hilt Sample)

A sample repository module demonstrating the use of **Kilt Generate Hilt KSP** for automatic Hilt module generation with Pokemon API.

## Overview

This module showcases how to use the `@KiltGenerateModule` annotation to automatically generate Hilt modules for dependency injection without writing
boilerplate code.

## Features

- 🚀 **Automatic Hilt Module Generation** using `@KiltGenerateModule`
- 🎯 **Pokemon API Integration** with Retrofit
- 📦 **Repository Pattern** implementation
- 🧪 **Comprehensive Testing** with MockK
- ✅ **Type-safe Network Calls** with Result wrapper

## API

This module uses the [PokeAPI](https://pokeapi.co/) to fetch Pokemon data.

### Endpoints Used

- `GET /pokemon` - List Pokemon with pagination
- `GET /pokemon/{id}` - Get Pokemon details by ID
- `GET /pokemon/{name}` - Get Pokemon details by name

## Architecture

```
PokemonRepository (Interface)
    ↑
PokemonRepositoryImpl
    ↓
PokeApiService (Retrofit)
    ↓  
Pokemon API
```

## Key Files

### Generated Module (Automatic)

```kotlin
// Generated: tech.thdev.kilt.poke.data.hilt.PokemonRepositoryImplModule
@Module
@InstallIn(SingletonComponent::class)
interface PokemonRepositoryImplModule {
    @Singleton
    @Binds
    abstract fun bindPokemonRepository(
        pokemonRepository: PokemonRepositoryImpl
    ): PokemonRepository
}
```

### Repository Implementation

```kotlin
@Singleton
@KiltGenerateModule  // ← This generates the module above!
class PokemonRepositoryImpl @Inject constructor(
    private val apiService: PokeApiService
) : PokemonRepository {
    // Implementation...
}
```

## Usage Example

### In your ViewModel

```kotlin
@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository // Automatically injected!
) : ViewModel() {
    
    fun loadPokemonList() {
        viewModelScope.launch {
            repository.getPokemonList().fold(
                onSuccess = { response -> 
                    // Handle success
                },
                onFailure = { error ->
                    // Handle error
                }
            )
        }
    }
}
```

### In your Activity/Fragment

```kotlin
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    @Inject
    lateinit var repository: PokemonRepository // Ready to use!
    
    // ...
}
```

## API Examples

### Get Pokemon List

```kotlin
val result = repository.getPokemonList(limit = 20, offset = 0)
result.fold(
    onSuccess = { response ->
        println("Found ${response.results.size} Pokemon")
        response.results.forEach { pokemon ->
            println("${pokemon.name} (ID: ${pokemon.id})")
        }
    },
    onFailure = { error ->
        println("Error: ${error.message}")
    }
)
```

### Get Pokemon Details

```kotlin
val result = repository.getPokemonById(1) // Bulbasaur
result.fold(
    onSuccess = { pokemon ->
        println("Pokemon: ${pokemon.name}")
        println("Height: ${pokemon.height}")
        println("Weight: ${pokemon.weight}")
        println("Types: ${pokemon.types.map { it.type.name }}")
    },
    onFailure = { error ->
        println("Error: ${error.message}")
    }
)
```

## Testing

The module includes comprehensive unit tests using MockK:

```bash
./gradlew :core-app:data:poke-repository:test
```

### Test Coverage

- ✅ Successful API responses
- ✅ Error handling
- ✅ Multiple Pokemon fetching
- ✅ Name-based lookup
- ✅ Parameter validation

## Dependencies

This module depends on:

- **Kilt Generate Hilt KSP** - For automatic module generation
- **Hilt** - For dependency injection
- **Retrofit** - For network requests
- **Gson** - For JSON parsing
- **OkHttp** - For HTTP client
- **Coroutines** - For asynchronous operations

## Benefits of Using Kilt

### Before Kilt (Manual)

```kotlin
// You would need to manually create this module:
@Module
@InstallIn(SingletonComponent::class)
interface PokemonRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindPokemonRepository(
        impl: PokemonRepositoryImpl
    ): PokemonRepository
}
```

### After Kilt (Automatic)

```kotlin
// Just add the annotation and module is generated automatically!
@Singleton
@KiltGenerateModule
class PokemonRepositoryImpl @Inject constructor(
    private val apiService: PokeApiService
) : PokemonRepository
```

## Related Modules

- [`kilt-generate-hilt-ksp`](../../../core/kilt-generate-hilt-ksp) - The KSP processor used by this module
- [`kilt-generate-annotations`](../../../core/kilt-generate-annotations) - Contains the `@KiltGenerateModule` annotation