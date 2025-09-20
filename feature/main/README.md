# Feature Main

A feature module that displays the main Pokemon list screen using Jetpack Compose and **poke-repository-api**.

## Overview

This module demonstrates a complete feature implementation using:

- **MVVM Architecture** with Compose
- **Hilt Dependency Injection**
- **Pokemon Repository API** integration
- **Modern UI/UX** with Material 3 Design
- **Comprehensive Testing** with Mockito and Turbine

## Features

- 🎯 **Pokemon List Display** - Browse all Pokemon with pagination
- 🔄 **Pull-to-Refresh** - Refresh the list by pulling down
- ♾️ **Infinite Scrolling** - Automatic loading when scrolling near bottom
- 🖼️ **Image Loading** - Pokemon sprites with Coil
- ⚡ **Loading States** - Proper loading indicators
- 🚨 **Error Handling** - User-friendly error messages with retry
- 📱 **Material 3 Design** - Modern Android design system

## Architecture

```
MainScreen (Compose UI)
    ↓
MainViewModel (State Management)
    ↓  
PokemonRepository (Data Layer)
    ↓
Pokemon API (Network)
```

## Key Components

### 📱 **MainScreen.kt**

- Modern Compose UI with Material 3
- Pull-to-refresh functionality
- Infinite scrolling with lazy loading
- Error states with retry mechanism
- Loading indicators

### 🧠 **MainViewModel.kt**

- `@HiltViewModel` for automatic injection
- StateFlow-based reactive state management
- Pagination handling
- Error recovery
- Memory-efficient data loading

### 📊 **MainUiState.kt**

- Immutable UI state representation
- Helper functions for state checks
- Pokemon item state modeling
- Extension functions for data mapping

## UI States

### **Initial Loading**

```kotlin
MainUiState(
    isLoading = true,
    pokemonList = emptyList(),
    error = null
)
```

### **Success with Data**

```kotlin
MainUiState(
    isLoading = false,
    pokemonList = listOf(/* Pokemon items */),
    hasMore = true,
    nextUrl = "https://pokeapi.co/api/v2/pokemon/?offset=20&limit=20"
)
```

### **Error State**

```kotlin
MainUiState(
    isLoading = false,
    pokemonList = emptyList(),
    error = "Network connection failed"
)
```

## Dependencies

- **poke-repository-api** - Pokemon data access
- **Jetpack Compose** - Modern UI toolkit
- **Hilt** - Dependency injection
- **Coil** - Image loading
- **Material 3** - Design system

## Usage in Navigation

```kotlin
// In your NavHost setup
NavHost(/*...*/) {
    mainScreen() // Extension function from MainNavigation.kt
}

// Or navigate programmatically
navController.navigate(MainRoute)
```

## Testing

### **ViewModel Tests (MainViewModelTest)**

- ✅ Initial state verification
- ✅ Loading state transitions
- ✅ Success state handling
- ✅ Error state management
- ✅ Pagination logic
- ✅ Refresh functionality
- ✅ Retry mechanism

### **Test Coverage**

```bash
./gradlew :feature:main:testDebugUnitTest --tests="*MainViewModelTest"
```

### **Key Test Scenarios**

- Initial data loading
- Error handling and recovery
- Pull-to-refresh behavior
- Infinite scroll pagination
- State transitions with Turbine

## UI Preview

### Pokemon List Screen

- **Header**: "Pokédex" with Material 3 styling
- **List Items**: Pokemon cards with:
    - Pokemon sprite image (80x80dp)
    - Pokemon name (capitalized)
    - Pokemon ID number (#001, #002, etc.)
- **Loading**: Progress indicators with text
- **Error**: Friendly error message with retry button

### Interactive Elements

- **Pull-to-Refresh**: Swipe down to refresh
- **Infinite Scroll**: Loads more when near bottom
- **Retry Button**: Tap to retry after error
- **Pokemon Cards**: Tap to view details (future feature)

## Integration Example

```kotlin
// In your app module
@HiltAndroidApp
class MyApplication : Application()

// In MainActivity
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                MainScreen() // Automatically gets injected dependencies
            }
        }
    }
}
```

## Development Notes

### **State Management**

- Uses `MutableStateFlow` for internal state
- Exposes `StateFlow` for UI observation
- Immutable state updates with `copy()`

### **Pagination Strategy**

- Load 20 items per page
- Start loading more when 5 items from bottom
- Append new items to existing list
- Handle end-of-data gracefully

### **Error Handling**

- Network errors with retry option
- Loading states prevent duplicate requests
- User-friendly error messages

### **Performance Optimizations**

- Lazy loading with `LazyColumn`
- Image caching with Coil
- Efficient state updates
- Memory-conscious pagination

## Future Enhancements

- 🔍 **Search functionality**
- 🏷️ **Filter by Pokemon type**
- ❤️ **Favorites system**
- 📱 **Pokemon detail screen**
- 🌙 **Dark theme support**
- 💾 **Offline caching**

## Related Modules

- [`poke-repository-api`](../../core-app/data/poke-repository-api) - Data layer interface
- [`poke-repository`](../../core-app/data/poke-repository) - Repository implementation
- [`kilt-generate-hilt-ksp`](../../core/kilt-generate-hilt-ksp) - Automatic Hilt module generation