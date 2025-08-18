package tech.thdev.kilt.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import tech.thdev.kilt.ksp.internal.KiltFactoryConst
import tech.thdev.kilt.ksp.internal.generate.generateDaggerModule
import tech.thdev.kilt.ksp.internal.visitor.kiltGenerateAnnotationVisitor

/**
 * 문서 참고를 해서 해석하는게 좋습니다.
 * https://square.github.io/kotlinpoet/
 */
class KiltFactoryProcessor(
    private val codeGenerator: CodeGenerator,
    private val testOnly: Boolean,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(KiltFactoryConst.KILT_GENERATE_MODULE.canonicalName)
            .kiltGenerateAnnotationVisitor(testOnly = testOnly)
            // 찾은 심볼 목록을 순회하며 각각 파일을 생성
            .forEach { model ->
                generateDaggerModule(
                    codeGenerator = codeGenerator,
                    model = model,
                )
            }

        return emptyList()
    }
}
