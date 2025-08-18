package tech.thdev.kilt.ksp.internal.generate

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import tech.thdev.kilt.ksp.internal.KiltFactoryConst
import tech.thdev.kilt.ksp.internal.mergePrefixBind
import tech.thdev.kilt.ksp.internal.mergeSuffixDaggerModule
import tech.thdev.kilt.ksp.internal.model.ResearchRepositoryModel
import tech.thdev.kilt.ksp.internal.removeSuffixImpl
import java.io.OutputStreamWriter

internal fun generateDaggerModule(
    codeGenerator: CodeGenerator,
    model: ResearchRepositoryModel,
) {
    val targetClass = model.targetClass
    val targetClassName = ClassName(targetClass.packageName.asString(), targetClass.simpleName.getShortName())
    val generateFileName = targetClassName.simpleName.mergeSuffixDaggerModule()
    val generatePackageName = "${targetClassName.packageName}.dagger"

    val fileSpecBuilder = FileSpec.builder(generatePackageName, generateFileName)

    // 수정됨: when 식에 모든 스코프 처리 로직 복원
    val (hiltComponent, scopeAnnotation) = when {
        model.singleton -> KiltFactoryConst.SINGLETON_COMPONENT to KiltFactoryConst.DAGGER_REUSABLE
        model.activityRetained -> KiltFactoryConst.ACTIVITY_RETAINED_COMPONENT to KiltFactoryConst.DAGGER_ACTIVITY_RETAINED_SCOPED
        model.viewModel -> KiltFactoryConst.VIEW_MODEL_COMPONENT to KiltFactoryConst.DAGGER_VIEW_MODEL_SCOPED
        else -> KiltFactoryConst.ACTIVITY_COMPONENT to null
    }

    val moduleInterfaceBuilder = TypeSpec.interfaceBuilder(generateFileName)
        .addModifiers(KModifier.PUBLIC)
        .addAnnotation(KiltFactoryConst.DAGGER_MODULE)
        .addAnnotation(
            AnnotationSpec.builder(KiltFactoryConst.DAGGER_INSTALL_IN)
                .addMember("%T::class", hiltComponent)
                .build()
        )

    model.parentsList.forEach { parentInterface ->
        val funSpec = FunSpec.builder(parentInterface.simpleName.mergePrefixBind())
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(KiltFactoryConst.DAGGER_BINDS)
            .addParameter(
                ParameterSpec.builder(
                    name = targetClassName.simpleName.replaceFirstChar { it.lowercase() }.removeSuffixImpl(),
                    type = targetClassName
                ).build()
            )
            .returns(parentInterface)

        scopeAnnotation?.let {
            funSpec.addAnnotation(it)
        }

        moduleInterfaceBuilder.addFunction(funSpec.build())
    }

    fileSpecBuilder.addType(moduleInterfaceBuilder.build())

    runCatching {
        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(true, targetClass.containingFile!!),
            packageName = generatePackageName,
            fileName = generateFileName
        )
        OutputStreamWriter(file, "UTF-8").use { writer ->
            fileSpecBuilder.build().writeTo(writer)
        }
    }
}
