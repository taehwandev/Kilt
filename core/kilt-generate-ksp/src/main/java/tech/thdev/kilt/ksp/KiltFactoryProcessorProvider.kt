package tech.thdev.kilt.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class KiltFactoryProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        KiltFactoryProcessor(
            codeGenerator = environment.codeGenerator,
            testOnly = environment.options["isTest"] == "Y",
        )
}
