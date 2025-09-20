package tech.thdev.kilt.hilt.ksp.internal.visitor

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import tech.thdev.kilt.hilt.ksp.internal.KiltFactoryConst
import tech.thdev.kilt.hilt.ksp.internal.model.ResearchRepositoryModel

/**
 * Visitor function for processing classes annotated with @KiltGenerateModule.
 *
 * This function analyzes annotated classes to extract information needed for
 * Dagger module generation, including implemented interfaces and scope annotations.
 *
 * @param testOnly Whether running in test mode (skips validation if true)
 * @return List of [ResearchRepositoryModel] containing processed class information
 */
internal fun Sequence<KSAnnotated>.kiltGenerateAnnotationVisitor(
  testOnly: Boolean,
): List<ResearchRepositoryModel> =
  this.filterIsInstance<KSClassDeclaration>()
    .filter { classDeclaration ->
      // Skip validation in test mode, otherwise validate the class
      classDeclaration.validate { _, _ -> testOnly.not() }
    }
    .mapNotNull { classDeclaration ->
      processClassDeclaration(classDeclaration)
    }
    .filter { model ->
      // Only include models that are valid for generation
      model.isValidForGeneration()
    }
    .toList()

/**
 * Processes a single class declaration to create a [ResearchRepositoryModel].
 *
 * @param classDeclaration The class declaration to process
 * @return [ResearchRepositoryModel] if the class implements interfaces, null otherwise
 */
private fun processClassDeclaration(classDeclaration: KSClassDeclaration): ResearchRepositoryModel? {
  // Extract parent interfaces
  val parentInterfaces = extractParentInterfaces(classDeclaration)

  if (parentInterfaces.isEmpty()) {
    return null
  }

  // Extract scope annotations
  val scopeInfo = extractScopeAnnotations(classDeclaration)

  return ResearchRepositoryModel(
    parentsList = parentInterfaces,
    targetClass = classDeclaration,
    singleton = scopeInfo.singleton,
    activityRetained = scopeInfo.activityRetained,
    viewModel = scopeInfo.viewModel,
  )
}

/**
 * Extracts parent interfaces from a class declaration.
 *
 * @param classDeclaration The class declaration to analyze
 * @return List of [ClassName] representing the implemented interfaces
 */
private fun extractParentInterfaces(classDeclaration: KSClassDeclaration): List<ClassName> =
  classDeclaration.superTypes
    .map { it.resolve().declaration }
    .mapNotNull { superType ->
      val packageName = superType.packageName.asString()
      val simpleName = superType.simpleName.getShortName()

      // Only include valid package and class names
      if (packageName.isNotBlank() && simpleName.isNotBlank()) {
        ClassName(packageName, simpleName)
      } else {
        null
      }
    }
    .toList()

/**
 * Data class to hold scope annotation information.
 */
private data class ScopeInfo(
  val singleton: Boolean,
  val activityRetained: Boolean,
  val viewModel: Boolean,
)

/**
 * Extracts scope annotation information from a class declaration.
 *
 * @param classDeclaration The class declaration to analyze
 * @return [ScopeInfo] containing the scope annotation flags
 */
private fun extractScopeAnnotations(classDeclaration: KSClassDeclaration): ScopeInfo =
  ScopeInfo(
    singleton = classDeclaration.annotations.hasAnnotation(KiltFactoryConst.DAGGER_SINGLETON.simpleName),
    activityRetained = classDeclaration.annotations.hasAnnotation(KiltFactoryConst.DAGGER_ACTIVITY_RETAINED_SCOPED.simpleName),
    viewModel = classDeclaration.annotations.hasAnnotation(KiltFactoryConst.DAGGER_VIEW_MODEL_SCOPED.simpleName),
  )

/**
 * Extension function to check if a sequence of annotations contains a specific annotation.
 *
 * @param target The simple name of the annotation to search for
 * @return true if the annotation is found, false otherwise
 */
private fun Sequence<KSAnnotation>.hasAnnotation(target: String): Boolean =
  any { annotation ->
    annotation.shortName.asString() == target
  }
