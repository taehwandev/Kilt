package tech.thdev.kilt.hilt.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import tech.thdev.kilt.hilt.ksp.internal.KiltFactoryConst
import tech.thdev.kilt.hilt.ksp.internal.generate.generateDaggerModule
import tech.thdev.kilt.hilt.ksp.internal.visitor.kiltGenerateAnnotationVisitor

/**
 * Kotlin Symbol Processor (KSP) for automatically generating Dagger Hilt modules.
 *
 * This processor analyzes classes annotated with [KiltGenerateModule] and generates
 * corresponding Dagger modules that bind the implementation classes to their interfaces.
 *
 * The processor:
 * 1. Finds all classes annotated with @KiltGenerateModule
 * 2. Analyzes their scope annotations (@Singleton, @ActivityRetainedScoped, etc.)
 * 3. Detects implemented interfaces
 * 4. Generates appropriate Dagger modules with @Binds methods
 *
 * For implementation details, refer to:
 * https://square.github.io/kotlinpoet/
 *
 * @param codeGenerator The KSP code generator for creating new files
 * @param logger The KSP logger for debugging and error reporting
 * @param testOnly Flag indicating if running in test mode
 */
class KiltHiltFactoryProcessor(
  private val codeGenerator: CodeGenerator,
  private val logger: KSPLogger,
  private val testOnly: Boolean = false,
) : SymbolProcessor {

  /**
   * Processes all symbols with @KiltGenerateModule annotation and generates Dagger modules.
   *
   * @param resolver The KSP resolver for symbol analysis
   * @return List of symbols that couldn't be processed (empty in normal cases)
   */
  override fun process(resolver: Resolver): List<KSAnnotated> {
    try {
      val annotatedSymbols = resolver.getSymbolsWithAnnotation(
        KiltFactoryConst.KILT_GENERATE_MODULE.canonicalName,
      )

      val models = annotatedSymbols.kiltGenerateAnnotationVisitor(testOnly = testOnly)

      logger.info("Found ${models.size} classes to process for Dagger module generation")

      // Generate Dagger module for each discovered model
      models.forEach { model ->
        try {
          generateDaggerModule(
            codeGenerator = codeGenerator,
            model = model,
          )
          logger.info("Successfully generated module for ${model.targetClass.simpleName.asString()}")
        } catch (e: Exception) {
          logger.error("Failed to generate module for ${model.targetClass.simpleName.asString()}: ${e.message}", model.targetClass)
        }
      }
    } catch (e: Exception) {
      logger.error("Error during processing: ${e.message}")
    }

    return emptyList()
  }
}
