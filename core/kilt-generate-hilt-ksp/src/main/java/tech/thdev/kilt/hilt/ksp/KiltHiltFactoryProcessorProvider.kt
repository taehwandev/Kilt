package tech.thdev.kilt.hilt.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Provider for [KiltHiltFactoryProcessor] that creates instances of the symbol processor.
 *
 * This class is used by the KSP framework to instantiate the processor with the
 * appropriate environment and configuration.
 */
class KiltHiltFactoryProcessorProvider : SymbolProcessorProvider {

  /**
   * Creates a new instance of [KiltHiltFactoryProcessor].
   *
   * @param environment The KSP environment containing code generator, logger, and options
   * @return A configured instance of [KiltHiltFactoryProcessor]
   */
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
    KiltHiltFactoryProcessor(
      codeGenerator = environment.codeGenerator,
      logger = environment.logger,
      testOnly = environment.options["isTest"] == "Y",
    )
}
