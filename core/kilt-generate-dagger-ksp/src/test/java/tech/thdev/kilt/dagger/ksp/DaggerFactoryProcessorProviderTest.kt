package tech.thdev.kilt.dagger.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Test class for [KiltDaggerFactoryProcessorProvider] and [KiltDaggerFactoryProcessor].
 *
 * These tests verify that the KSP processor correctly generates Dagger modules
 * for classes annotated with @KiltGenerateModule with various scope configurations.
 */
@OptIn(ExperimentalCompilerApi::class)
internal class DaggerFactoryProcessorProviderTest {

  @get:Rule
  val tempFolder = TemporaryFolder()

  @Test
  fun `test generate Dagger module with @Singleton scope`() {
    // Given
    val kiltAnnotation = createKiltAnnotationSource()
    val daggerAnnotations = createDaggerAnnotationsSource()
    val reusableAnnotation = createReusableAnnotationSource()
    val repositoryInterface = createRepositoryInterfaceSource()
    val repositoryImpl = createSingletonRepositoryImplSource()

    // When
    val result = compile(kiltAnnotation, daggerAnnotations, reusableAnnotation, repositoryInterface, repositoryImpl)

    // Then - Verify that our KSP processor successfully found and processed the annotated class
    val messages = result.messages

    // Check if the KSP processor ran and found classes to process
    val foundClassesMessage = messages.contains("Found") && messages.contains("classes to process")
    val generatedModuleMessage = messages.contains("Successfully generated") && messages.contains("Dagger module")

    // For debugging - print actual messages if assertions fail
    if (!foundClassesMessage || !generatedModuleMessage) {
      System.err.println("Actual messages: $messages")
    }

    assertTrue("KSP should find classes to process (found: $foundClassesMessage)", foundClassesMessage)
    assertTrue("KSP should successfully generate Dagger module (found: $generatedModuleMessage)", generatedModuleMessage)
  }

  @Test
  fun `test generate Dagger module with @Reusable scope`() {
    // Given
    val kiltAnnotation = createKiltAnnotationSource()
    val daggerAnnotations = createDaggerAnnotationsSource()
    val reusableAnnotation = createReusableAnnotationSource()
    val repositoryInterface = createRepositoryInterfaceSource()
    val repositoryImpl = createReusableRepositoryImplSource()

    // When
    val result = compile(kiltAnnotation, daggerAnnotations, reusableAnnotation, repositoryInterface, repositoryImpl)

    // Then - Verify processor works for Reusable scope
    val messages = result.messages
    val foundClassesMessage = messages.contains("Found") && messages.contains("classes to process")
    val generatedModuleMessage = messages.contains("Successfully generated") && messages.contains("Dagger module")

    assertTrue("KSP should process Reusable scoped classes", foundClassesMessage)
    assertTrue("KSP should generate Dagger module for Reusable scope", generatedModuleMessage)
  }

  @Test
  fun `test generate Dagger module with no scope`() {
    // Given
    val kiltAnnotation = createKiltAnnotationSource()
    val daggerAnnotations = createDaggerAnnotationsSource()
    val reusableAnnotation = createReusableAnnotationSource()
    val repositoryInterface = createRepositoryInterfaceSource()
    val repositoryImpl = createUnscopedRepositoryImplSource()

    // When
    val result = compile(kiltAnnotation, daggerAnnotations, reusableAnnotation, repositoryInterface, repositoryImpl)

    // Then - Verify processor works for unscoped classes
    val messages = result.messages
    val foundClassesMessage = messages.contains("Found") && messages.contains("classes to process")
    val generatedModuleMessage = messages.contains("Successfully generated") && messages.contains("Dagger module")

    assertTrue("KSP should process unscoped classes", foundClassesMessage)
    assertTrue("KSP should generate Dagger module for unscoped classes", generatedModuleMessage)
  }

  private fun createKiltAnnotationSource(): SourceFile {
    return SourceFile.kotlin(
      "KiltAnnotations.kt",
      buildString {
        appendLine("package tech.thdev.kilt.annotations")
        appendLine("")
        appendLine("@Retention(AnnotationRetention.SOURCE)")
        appendLine("@Target(AnnotationTarget.CLASS)")
        appendLine("annotation class KiltGenerateModule")
      }
    )
  }

  private fun createDaggerAnnotationsSource(): SourceFile {
    return SourceFile.kotlin(
      "DaggerAnnotations.kt",
      buildString {
        appendLine("package javax.inject")
        appendLine("annotation class Inject")
        appendLine("annotation class Singleton")
      }
    )
  }

  private fun createReusableAnnotationSource(): SourceFile {
    return SourceFile.kotlin(
      "ReusableAnnotation.kt",
      buildString {
        appendLine("package dagger")
        appendLine("annotation class Reusable")
      }
    )
  }

  private fun createRepositoryInterfaceSource(): SourceFile {
    return SourceFile.kotlin(
      "SampleRepository.kt",
      buildString {
        appendLine("package com.example.repository")
        appendLine("interface SampleRepository")
      }
    )
  }

  private fun createSingletonRepositoryImplSource(): SourceFile {
    return SourceFile.kotlin(
      "SampleRepositoryImpl.kt",
      buildString {
        appendLine("package com.example.repository.impl")
        appendLine("import com.example.repository.SampleRepository")
        appendLine("import tech.thdev.kilt.annotations.KiltGenerateModule")
        appendLine("import javax.inject.Inject")
        appendLine("import javax.inject.Singleton")
        appendLine("")
        appendLine("@Singleton")
        appendLine("@KiltGenerateModule")
        appendLine("class SampleRepositoryImpl @Inject constructor() : SampleRepository")
      }
    )
  }

  private fun createReusableRepositoryImplSource(): SourceFile {
    return SourceFile.kotlin(
      "SampleRepositoryImpl.kt",
      buildString {
        appendLine("package com.example.repository.impl")
        appendLine("import com.example.repository.SampleRepository")
        appendLine("import tech.thdev.kilt.annotations.KiltGenerateModule")
        appendLine("import javax.inject.Inject")
        appendLine("import dagger.Reusable")
        appendLine("")
        appendLine("@Reusable")
        appendLine("@KiltGenerateModule")
        appendLine("class SampleRepositoryImpl @Inject constructor() : SampleRepository")
      }
    )
  }

  private fun createUnscopedRepositoryImplSource(): SourceFile {
    return SourceFile.kotlin(
      "SampleRepositoryImpl.kt",
      buildString {
        appendLine("package com.example.repository.impl")
        appendLine("import com.example.repository.SampleRepository")
        appendLine("import tech.thdev.kilt.annotations.KiltGenerateModule")
        appendLine("import javax.inject.Inject")
        appendLine("")
        appendLine("@KiltGenerateModule")
        appendLine("class SampleRepositoryImpl @Inject constructor() : SampleRepository")
      }
    )
  }

  /**
   * Compiles the provided source files using KSP with the DaggerFactoryProcessorProvider.
   */
  private fun compile(vararg sourceFiles: SourceFile): KotlinCompilation.Result {
    return KotlinCompilation().apply {
      sources = sourceFiles.toList()
      symbolProcessorProviders = listOf(KiltDaggerFactoryProcessorProvider())
      kspArgs = mutableMapOf("isTest" to "Y")
      workingDir = tempFolder.root
      inheritClassPath = true
      verbose = false
    }.compile()
  }
}
