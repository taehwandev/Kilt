# Publishing to Maven Central

This guide explains how to publish libraries to Maven Central using the generic publication system. The system supports both project-wide and module-specific
configurations.

## Prerequisites

### 1. Create Sonatype Account

1. Go to [Sonatype OSSRH](https://issues.sonatype.org)
2. Create a JIRA account
3. Create a new project ticket for your namespace (e.g., `com.yourcompany.yourproject`)
4. Wait for approval (usually takes 1-2 business days)

### 2. Generate GPG Key

Generate a GPG key for signing artifacts:

```bash
# Generate GPG key
gpg --gen-key

# List keys to get key ID
gpg --list-keys

# Export public key to keyserver
gpg --keyserver hkp://keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Export private key
gpg --export-secret-keys YOUR_KEY_ID > secring.gpg
```

### 3. Set up Credentials

Create or update `local.properties` with:

```properties
# Sonatype credentials
ossrhUsername=your_sonatype_username
ossrhPassword=your_sonatype_password

# GPG signing configuration
signing.keyId=YOUR_GPG_KEY_ID
signing.password=YOUR_GPG_PASSPHRASE
signing.secretKeyRingFile=/path/to/secring.gpg
```

Or set environment variables for CI/CD:

```bash
export OSSRH_USERNAME=your_username
export OSSRH_PASSWORD=your_password
export SIGNING_KEY_ID=your_key_id
export SIGNING_PASSWORD=your_passphrase
export SIGNING_SECRET_KEY_RING_FILE=/path/to/secring.gpg
```

## Configuration System

The publication system supports three levels of configuration:

1. **Default values** (hardcoded fallbacks)
2. **Project-wide settings** (`gradle.properties`)
3. **Module-specific settings** (`build.gradle.kts` extra properties)

### Project-wide Configuration

Add to `gradle.properties`:

```properties
# Publication Configuration
publication.groupId=com.yourcompany.yourproject
publication.description=Your project description
publication.url=https://github.com/yourusername/yourproject
publication.license.name=MIT License
publication.license.url=https://opensource.org/licenses/MIT
publication.developer.id=yourid
publication.developer.name=Your Name
publication.developer.email=your.email@example.com
publication.scm.connection=scm:git:git://github.com/yourusername/yourproject.git
publication.scm.developerConnection=scm:git:ssh://github.com:yourusername/yourproject.git
publication.scm.url=https://github.com/yourusername/yourproject
```

### Module-specific Configuration

Override settings in individual module's `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.tech.thdev.kotlin.library)
    alias(libs.plugins.tech.thdev.android.library.publish)
}

version = "1.0.0"

// Module-specific publication configuration
extra["publication.name"] = "Your Module Name"
extra["publication.description"] = "Specific description for this module"
extra["publication.artifactId"] = "custom-artifact-name"
```

## Available Configuration Properties

| Property | Description | Example |
|----------|-------------|---------|
| `publication.groupId` | Maven group ID | `com.yourcompany.yourproject` |
| `publication.artifactId` | Maven artifact ID | `your-module-name` |
| `publication.name` | Display name | `Your Library Name` |
| `publication.description` | Library description | `A useful Android library` |
| `publication.url` | Project URL | `https://github.com/user/project` |
| `publication.license.name` | License name | `MIT License` |
| `publication.license.url` | License URL | `https://opensource.org/licenses/MIT` |
| `publication.developer.id` | Developer ID | `yourid` |
| `publication.developer.name` | Developer name | `Your Name` |
| `publication.developer.email` | Developer email | `you@example.com` |
| `publication.scm.connection` | SCM connection | `scm:git:git://github.com/user/project.git` |
| `publication.scm.developerConnection` | SCM dev connection | `scm:git:ssh://github.com:user/project.git` |
| `publication.scm.url` | SCM URL | `https://github.com/user/project` |
| `publication.repository.release` | Release repository | Custom Sonatype URL |
| `publication.repository.snapshot` | Snapshot repository | Custom Sonatype URL |

## Apply Publication Plugin

Add the publication plugin to modules you want to publish:

```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.tech.thdev.kotlin.library) // or android.library
    alias(libs.plugins.tech.thdev.android.library.publish)
}

version = "1.0.0" // Required: Set module version
```

## Publishing Process

### 1. Prepare Release

Update version in the module's `build.gradle.kts`:

```kotlin
version = "1.0.0" // Change from "1.0.0-SNAPSHOT"
```

### 2. Build and Test

```bash
# Clean and build all modules
./gradlew clean build

# Run all tests
./gradlew test

# Verify publications
./gradlew publishToMavenLocal
```

### 3. Publish to Staging

Publish all configured modules:

```bash
# Publish all modules with publication plugin
./gradlew publish

# Or publish specific modules
./gradlew :your-module:publish
```

### 4. Release from Staging

1. Go to [Sonatype Nexus Repository Manager](https://s01.oss.sonatype.org/)
2. Log in with your credentials
3. Navigate to "Staging Repositories"
4. Find your repository (usually based on your group ID)
5. Select it and click "Close"
6. Wait for validation to complete
7. Click "Release" to publish to Maven Central

### 5. Verify Publication

After release (may take 2-10 minutes), verify on:

- [Maven Central Search](https://search.maven.org/)
- [MVN Repository](https://mvnrepository.com/)

## CI/CD Integration

### GitHub Actions Example

The system supports both file-based and in-memory GPG keys:

```yaml
name: Publish to Maven Central

on:
  release:
    types: [published]

jobs:
  publish:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Publish to Maven Central
      run: ./gradlew publish
      env:
        OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
        OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
        SIGNING_KEY_ID: ${{ secrets.SIGNING_KEY_ID }}
        SIGNING_PASSWORD: ${{ secrets.SIGNING_PASSWORD }}
        # Option 1: Use in-memory key (recommended)
        SIGNING_KEY: ${{ secrets.SIGNING_KEY }}
        # Option 2: Use key file
        # SIGNING_SECRET_KEY_RING_FILE: ${{ secrets.SIGNING_SECRET_KEY_RING_FILE }}
```

## Environment Variable Mapping

The system automatically maps environment variables:

| Property | Environment Variable |
|----------|---------------------|
| `ossrhUsername` | `OSSRH_USERNAME` |
| `signing.keyId` | `SIGNING_KEY_ID` |
| `publication.groupId` | `PUBLICATION_GROUPID` |
| etc. | (property name uppercased with dots replaced by underscores) |

## Usage After Publishing

Once published, users can include the libraries:

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.yourcompany.yourproject:your-module:1.0.0")
}
```

### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'com.yourcompany.yourproject:your-module:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.yourcompany.yourproject</groupId>
    <artifactId>your-module</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Multi-Project Setup

For projects with multiple publishable modules:

### 1. Project Structure

```
your-project/
├── gradle.properties              # Project-wide publication config
├── local.properties              # Credentials (not committed)
├── module-a/
│   └── build.gradle.kts          # Module-specific config
├── module-b/
│   └── build.gradle.kts          # Module-specific config
└── internal-module/              # No publication plugin = not published
    └── build.gradle.kts
```

### 2. Selective Publishing

Only modules with the publication plugin applied will be published:

```kotlin
// This module will be published
plugins {
    alias(libs.plugins.tech.thdev.kotlin.library)
    alias(libs.plugins.tech.thdev.android.library.publish) // ← Publication enabled
}
```

```kotlin
// This module will NOT be published
plugins {
    alias(libs.plugins.tech.thdev.kotlin.library)
    // No publication plugin = stays private
}
```

## Troubleshooting

### Common Issues

1. **Module Not Published**
    - Ensure the publication plugin is applied
    - Check that `version` is set in build.gradle.kts
    - Verify credentials are configured

2. **Wrong Artifact Name**
    - Set `extra["publication.artifactId"] = "custom-name"` in build.gradle.kts
    - Or configure `publication.artifactId` in gradle.properties

3. **Missing Metadata**
    - Configure publication properties in gradle.properties
    - Override specific properties in module build.gradle.kts if needed

### Debug Commands

```bash
# Check what will be published
./gradlew publishToMavenLocal --dry-run

# Verbose publishing
./gradlew publish --info --stacktrace

# List all publications
./gradlew tasks --group=publishing
```

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** in CI/CD
3. **Prefer in-memory GPG keys** for CI/CD
4. **Use GitHub secrets** for sensitive data
5. **Rotate credentials** periodically

## Support

For publishing issues:

1. Check [Sonatype OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
2. Review [Maven Central requirements](https://central.sonatype.org/publish/requirements/)
3. Contact Sonatype support through JIRA
4. Check GitHub Actions logs for CI/CD issues