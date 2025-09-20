import com.android.build.gradle.LibraryExtension
import java.util.Properties

plugins {
  `maven-publish`
  signing
}

// Configuration class for publication settings
data class PublicationConfig(
  val groupId: String = "tech.thdev",
  val artifactId: String = project.name,
  val projectName: String = project.name,
  val projectDescription: String = "Android/Kotlin library",
  val projectUrl: String = "https://github.com/taehwandev/AndroidLibrary",
  val licenseName: String = "MIT License",
  val licenseUrl: String = "https://opensource.org/licenses/MIT",
  val developerId: String = "taehwandev",
  val developerName: String = "TaeHwan",
  val developerEmail: String = "taehwan@thdev.tech",
  val scmConnection: String = "scm:git:git://github.com/taehwandev/AndroidLibrary.git",
  val scmDeveloperConnection: String = "scm:git:ssh://github.com:taehwandev/AndroidLibrary.git",
  val scmUrl: String = "https://github.com/taehwandev/AndroidLibrary"
)

// Read properties from gradle.properties or local.properties
val localProperties = Properties().apply {
  val localPropertiesFile = rootProject.file("local.properties")
  if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { load(it) }
  }
}

fun getPropertyValue(key: String): String? {
  return project.findProperty(key) as String?
    ?: localProperties.getProperty(key)
    ?: System.getenv(key.uppercase().replace(".", "_"))
}

fun getPublicationConfig(): PublicationConfig {
  return PublicationConfig(
    groupId = getPropertyValue("publication.groupId") ?: "tech.thdev",
    artifactId = getPropertyValue("publication.artifactId") ?: project.name,
    projectName = getPropertyValue("publication.name") ?: project.name,
    projectDescription = getPropertyValue("publication.description") ?: "Android/Kotlin library",
    projectUrl = getPropertyValue("publication.url") ?: "https://github.com/taehwandev/AndroidLibrary",
    licenseName = getPropertyValue("publication.license.name") ?: "MIT License",
    licenseUrl = getPropertyValue("publication.license.url") ?: "https://opensource.org/licenses/MIT",
    developerId = getPropertyValue("publication.developer.id") ?: "taehwandev",
    developerName = getPropertyValue("publication.developer.name") ?: "TaeHwan",
    developerEmail = getPropertyValue("publication.developer.email") ?: "taehwan@thdev.tech",
    scmConnection = getPropertyValue("publication.scm.connection") ?: "scm:git:git://github.com/taehwandev/AndroidLibrary.git",
    scmDeveloperConnection = getPropertyValue("publication.scm.developerConnection") ?: "scm:git:ssh://github.com:taehwandev/AndroidLibrary.git",
    scmUrl = getPropertyValue("publication.scm.url") ?: "https://github.com/taehwandev/AndroidLibrary"
  )
}

fun configurePublicationPom(pom: org.gradle.api.publish.maven.MavenPom, config: PublicationConfig) {
  pom.apply {
    name.set(config.projectName)
    description.set(config.projectDescription)
    url.set(config.projectUrl)

    licenses {
      license {
        name.set(config.licenseName)
        url.set(config.licenseUrl)
      }
    }

    developers {
      developer {
        id.set(config.developerId)
        name.set(config.developerName)
        email.set(config.developerEmail)
      }
    }

    scm {
      connection.set(config.scmConnection)
      developerConnection.set(config.scmDeveloperConnection)
      url.set(config.scmUrl)
    }
  }
}

fun configureSonatypeRepository(repositories: PublishingExtension) {
  repositories.repositories {
    maven {
      name = "sonatype"
      url = if (version.toString().endsWith("SNAPSHOT")) {
        uri(getPropertyValue("publication.repository.snapshot") ?: "https://s01.oss.sonatype.org/content/repositories/snapshots/")
      } else {
        uri(getPropertyValue("publication.repository.release") ?: "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
      }

      credentials {
        username = getPropertyValue("ossrhUsername") ?: System.getenv("OSSRH_USERNAME")
        password = getPropertyValue("ossrhPassword") ?: System.getenv("OSSRH_PASSWORD")
      }
    }
  }
}

fun configureSigning(signing: SigningExtension) {
  val signingKey = getPropertyValue("signing.keyId") ?: System.getenv("SIGNING_KEY_ID")
  val signingPassword = getPropertyValue("signing.password") ?: System.getenv("SIGNING_PASSWORD")
  val signingSecretKeyRingFile = getPropertyValue("signing.secretKeyRingFile") ?: System.getenv("SIGNING_SECRET_KEY_RING_FILE")
  val signingInMemoryKey = getPropertyValue("signing.key") ?: System.getenv("SIGNING_KEY")

  if (!signingInMemoryKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
    // Use in-memory key (preferred for CI/CD)
    signing.useInMemoryPgpKeys(signingKey, signingInMemoryKey, signingPassword)
    signing.sign(publishing.publications)
  } else if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank() && !signingSecretKeyRingFile.isNullOrBlank()) {
    // Use key file
    signing.sign(publishing.publications)
  }
}

val config = getPublicationConfig()
val libraryExtension = extensions.findByType<LibraryExtension>()

if (libraryExtension != null) {
  // Android Library Publication
  afterEvaluate {
    publishing {
      publications {
        create<MavenPublication>("release") {
          from(components["release"])

          groupId = config.groupId
          artifactId = config.artifactId
          version = project.version.toString()

          // Add sources jar
          val sourcesJar = tasks.register<Jar>("sourcesJar") {
            archiveClassifier.set("sources")
            from(libraryExtension.sourceSets.getByName("main").java.srcDirs)
          }
          artifact(sourcesJar)

          // Add javadoc jar
          val javadocJar = tasks.register<Jar>("javadocJar") {
            archiveClassifier.set("javadoc")
            // Android libraries typically use empty javadoc jar
          }
          artifact(javadocJar)

          configurePublicationPom(pom, config)
        }
      }

      configureSonatypeRepository(this)
    }

    configureSigning(signing)
  }
} else {
  // Kotlin/JVM Library Publication
  val javaComponent = components.findByName("java")
  if (javaComponent != null) {
    afterEvaluate {
      publishing {
        publications {
          create<MavenPublication>("maven") {
            from(javaComponent)

            groupId = config.groupId
            artifactId = config.artifactId
            version = project.version.toString()

            // Add sources jar for Kotlin/JVM
            val sourcesJar = tasks.register<Jar>("sourcesJar") {
              archiveClassifier.set("sources")
              from(project.the<SourceSetContainer>()["main"].allSource)
            }
            artifact(sourcesJar)

            // Add javadoc jar
            val javadocJar = tasks.register<Jar>("javadocJar") {
              archiveClassifier.set("javadoc")
            }
            artifact(javadocJar)

            configurePublicationPom(pom, config)
          }
        }

        configureSonatypeRepository(this)
      }

      configureSigning(signing)
    }
  }
}
