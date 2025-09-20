# Feature Home

The main entry point and navigation controller for the Kilt Pokemon application. This module serves as the host for all feature modules and manages
application-level navigation.

## Overview

This module provides:

- **Main Activity** - Entry point with Hilt integration
- **Application Class** - Hilt setup and app initialization
- **Navigation Management** - Centralized navigation between features
- **Theme Management** - Material 3 theming
- **Edge-to-Edge Support** - Modern Android display

## Architecture

```
KiltApplication (@HiltAndroidApp)
    ↓
HomeActivity (@AndroidEntryPoint)
    ↓
HomeNavigation (Navigation Controller)
    ↓
Feature Modules (main, detail, search, etc.)
```

## Key Components

### 🏠 **HomeActivity.kt**

- Main activity with `@AndroidEntryPoint`
- Edge-to-edge display support
- Material 3 theming integration
- Compose UI setup

### 📱 **KiltApplication.kt**

- Application class with `@HiltAndroidApp`
- Hilt dependency injection initialization
- App-level configuration

### 🧭 **HomeNavigation.kt**

- Centralized navigation management
- Feature module integration
- Route definitions
- Future extensibility

### 📍 **HomeNavigationRoutes.kt**

- Navigation route constants
- Type-safe navigation
- Route organization

## Features

### 🎯 **Main Entry Point**

- Launcher activity configuration
- Deep link support ready
- Intent filter setup

### 🏗️ **Hilt Integration**

- Automatic dependency injection
- Repository binding through Kilt
- ViewModel injection

### 📱 **Modern UI**

- Material 3 design system
- Edge-to-edge display
- Dark theme ready
- Responsive layout

### 🧭 **Navigation**

- Jetpack Navigation Compose
- Type-safe navigation
- Back stack management
- Deep linking support

## Module Structure

```
feature/home/
├── build.gradle.kts              # Module configuration
├── README.md                     # Documentation
├── src/main/
│   ├── AndroidManifest.xml      # Activity and permission declarations
│   └── java/tech/thdev/kilt/feature/home/
│       ├── HomeActivity.kt      # Main activity
│       ├── KiltApplication.kt   # Application class
│       ├── HomeNavigation.kt    # Navigation setup
│       └── HomeNavigationRoutes.kt # Route constants
└── src/test/
    └── java/tech/thdev/kilt/feature/home/
        └── HomeNavigationTest.kt # Navigation tests
```

## Dependencies

### **Feature Modules**

- `feature:main` - Pokemon list screen

### **Core Modules**

- `core-app:data:poke-repository-api` - Data layer interface
- `core-app:data:poke-repository` - Repository implementation

### **Android Libraries**

- Jetpack Compose
- Navigation Compose
- Hilt Android
- Activity Compose
- Material 3

## Usage

### **App Module Integration**

In your main app module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(projects.feature.home)
}
```

### **Application Class**

Replace your app's Application class reference:

```xml
<!-- AndroidManifest.xml -->
<application
    android:name="tech.thdev.kilt.feature.home.KiltApplication"
    ... >
```

### **Main Activity**

Set HomeActivity as launcher:

```xml
<activity
    android:name="tech.thdev.kilt.feature.home.HomeActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

## Navigation Flow

### **Current Navigation**

```
App Launch → HomeActivity → HomeNavigation → MainScreen (Pokemon List)
```

### **Future Navigation**

```
HomeActivity
├── MainScreen (Pokemon List)
├── PokemonDetailScreen  
├── SearchScreen
├── FavoritesScreen
└── SettingsScreen
```

## Extending Navigation

### **Adding New Screens**

1. Add route constant:

```kotlin
object HomeNavigationRoutes {
    const val POKEMON_DETAIL = "pokemon_detail/{pokemonId}"
}
```

2. Add to navigation graph:

```kotlin
fun HomeNavigation() {
    NavHost(...) {
        mainScreen()
        
        pokemonDetailScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
```

### **Navigation Parameters**

```kotlin
// Navigate with arguments
navController.navigate("pokemon_detail/25")

// Type-safe navigation (future enhancement)
navController.navigate(PokemonDetailRoute(pokemonId = 25))
```

## Permissions

### **Internet Access**

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### **Network State** (Optional)

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Theme Configuration

### **Material 3 Theme**

```kotlin
@Composable
private fun KiltTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
```

### **Customization**

Future theme customization can be added:

- Custom color schemes
- Typography scaling
- Dark/Light theme switching
- Dynamic color support

## Testing

### **Navigation Tests**

```bash
./gradlew :feature:home:testDebugUnitTest
```

### **Integration Tests**

Future integration tests can cover:

- Navigation between screens
- Deep link handling
- State preservation
- Back stack management

## Build Configuration

### **Gradle Setup**

```kotlin
plugins {
    alias(libs.plugins.tech.thdev.android.library.feature.compose)
    alias(libs.plugins.tech.thdev.android.library.navigation)
}
```

### **Namespace**

```kotlin
setNamespace("feature.home")
```

## Performance Considerations

### **Startup Optimization**

- Lazy initialization of non-critical components
- Efficient Hilt module structure
- Minimal Application.onCreate() work

### **Navigation Performance**

- Lazy loading of feature modules
- Efficient back stack management
- Memory-conscious screen transitions

## Future Enhancements

### **Deep Links**

- URL-based navigation
- Intent handling
- Universal links

### **Multi-Module Navigation**

- Feature-specific navigation graphs
- Nested navigation
- Modular routing

### **State Management**

- Navigation state preservation
- Deep link restoration
- Multi-back-stack support

## Development Guidelines

### **Adding New Features**

1. Create feature module
2. Add dependency in `build.gradle.kts`
3. Add navigation route
4. Implement navigation screen
5. Update tests

### **Navigation Patterns**

- Use sealed classes for complex routes
- Implement proper back handling
- Add loading states for async navigation
- Handle configuration changes

## Related Modules

- [`feature:main`](../main) - Pokemon list screen
- [`core-app:data:poke-repository-api`](../../core-app/data/poke-repository-api) - Data layer
- [`core:kilt-generate-hilt-ksp`](../../core/kilt-generate-hilt-ksp) - Hilt code generation