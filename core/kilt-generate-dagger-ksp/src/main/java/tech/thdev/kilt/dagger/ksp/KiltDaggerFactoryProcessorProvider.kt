package tech.thdev.kilt.dagger.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Provider for [KiltDaggerFactoryProcessor] that creates instances of the symbol processor.
 *
 * This class is used by the KSP framework to instantiate the processor with the
 * appropriate environment and configuration for Dagger module generation.
 */
class KiltDaggerFactoryProcessorProvider : SymbolProcessorProvider {

  /**
   * Creates a new instance of [KiltDaggerFactoryProcessor].
   *
   * @param environment The KSP environment containing code generator, logger, and options
   * @return A configured instance of [KiltDaggerFactoryProcessor]
   */
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
    KiltDaggerFactoryProcessor(
      codeGenerator = environment.codeGenerator,
      logger = environment.logger,
      testOnly = environment.options["isTest"] == "Y",
    )
}
