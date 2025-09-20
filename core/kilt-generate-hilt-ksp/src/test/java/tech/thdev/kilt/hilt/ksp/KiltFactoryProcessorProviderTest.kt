package tech.thdev.kilt.ksp

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
 * Test class for [KiltFactoryProcessorProvider] and [KiltHiltFactoryProcessor].
 *
 * These tests verify that the KSP processor correctly generates Dagger modules
 * for classes annotated with @KiltGenerateModule with various scope configurations.
 */
@OptIn(ExperimentalCompilerApi::class)
internal class KiltFactoryProcessorProviderTest {

  @get:Rule
  val tempFolder = TemporaryFolder()

  @Test
  fun `test generate module with @Singleton scope`() {
    // Given
    val kiltAnnotation = createKiltAnnotationSource()
    val injectAnnotations = createInjectAnnotationsSource()
    val hiltAnnotations = createHiltAnnotationsSource()
    val repositoryInterface = createRepositoryInterfaceSource()
    val repositoryImpl = createSingletonRepositoryImplSource()

    // When
    val result = compile(kiltAnnotation, injectAnnotations, hiltAnnotations, repositoryInterface, repositoryImpl)

    // Then - Verify that our KSP processor successfully found and processed the annotated class
    val messages = result.messages

    // Check if the KSP processor ran and found classes to process
    val foundClassesMessage = messages.contains("Found") && messages.contains("classes to process")
    val generatedModuleMessage = messages.contains("Successfully generated module")

    // For debugging - print actual messages if assertions fail
    if (!foundClassesMessage || !generatedModuleMessage) {
      System.err.println("Actual messages: $messages")
    }

    assertTrue("KSP should find classes to process (found: $foundClassesMessage)", foundClassesMessage)
    assertTrue("KSP should successfully generate module (found: $generatedModuleMessage)", generatedModuleMessage)
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

  private fun createInjectAnnotationsSource(): SourceFile {
    return SourceFile.kotlin(
      "InjectAnnotations.kt",
      buildString {
        appendLine("package javax.inject")
        appendLine("annotation class Inject")
        appendLine("annotation class Singleton")
      }
    )
  }

  private fun createHiltAnnotationsSource(): SourceFile {
    return SourceFile.kotlin(
      "HiltAnnotations.kt",
      buildString {
        appendLine("package dagger.hilt.android.scopes")
        appendLine("annotation class ActivityRetainedScoped")
        appendLine("annotation class ViewModelScoped")
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

  /**
   * Compiles the provided source files using KSP with the KiltFactoryProcessorProvider.
   */
  private fun compile(vararg sourceFiles: SourceFile): KotlinCompilation.Result {
    return KotlinCompilation().apply {
      sources = sourceFiles.toList()
      symbolProcessorProviders = listOf(KiltFactoryProcessorProvider())
      kspArgs = mutableMapOf("isTest" to "Y")
      workingDir = tempFolder.root
      inheritClassPath = true
      verbose = false
    }.compile()
  }
}
