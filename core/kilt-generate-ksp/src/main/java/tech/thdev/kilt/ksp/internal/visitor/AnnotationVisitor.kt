package tech.thdev.kilt.ksp.internal.visitor

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import tech.thdev.kilt.ksp.internal.KiltFactoryConst
import tech.thdev.kilt.ksp.internal.model.ResearchRepositoryModel

internal fun Sequence<KSAnnotated>.kiltGenerateAnnotationVisitor(
    testOnly: Boolean,
): List<ResearchRepositoryModel> =
    this.filterIsInstance<KSClassDeclaration>()
        .filter { it.validate { _, _ -> testOnly.not() } }
        .mapNotNull { classDeclaration ->
            val parents = classDeclaration.superTypes
                .map { it.resolve().declaration }
                .map { superType -> ClassName(superType.packageName.asString(), superType.simpleName.getShortName()) }
                .toList()

            if (parents.isEmpty()) {
                null
            } else {
                ResearchRepositoryModel(
                    parentsList = parents,
                    targetClass = classDeclaration,
                    singleton = classDeclaration.annotations.hasAnnotation(KiltFactoryConst.DAGGER_SINGLETON.simpleName),
                    activityRetained = classDeclaration.annotations.hasAnnotation(KiltFactoryConst.DAGGER_ACTIVITY_RETAINED_SCOPED.simpleName),
                    viewModel = classDeclaration.annotations.hasAnnotation(KiltFactoryConst.DAGGER_VIEW_MODEL_SCOPED.simpleName),
                )
            }
        }
        .toList()

private fun Sequence<KSAnnotation>.hasAnnotation(target: String): Boolean =
    any { it.shortName.asString() == target }
