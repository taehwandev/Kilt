package tech.thdev.kilt.dagger.ksp.internal.visitor

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import tech.thdev.kilt.dagger.ksp.internal.DaggerConstants
import tech.thdev.kilt.dagger.ksp.internal.model.DaggerRepositoryModel

/**
 * Visitor function for processing classes annotated with @KiltGenerateModule for Dagger.
 *
 * This function analyzes annotated classes to extract information needed for
 * Dagger module generation, including implemented interfaces and Dagger scope annotations.
 *
 * @param testOnly Whether running in test mode (skips validation if true)
 * @return List of [DaggerRepositoryModel] containing processed class information
 */
internal fun Sequence<KSAnnotated>.daggerGenerateAnnotationVisitor(
  testOnly: Boolean,
): List<DaggerRepositoryModel> =
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
 * Processes a single class declaration to create a [DaggerRepositoryModel].
 *
 * @param classDeclaration The class declaration to process
 * @return [DaggerRepositoryModel] if the class implements interfaces, null otherwise
 */
private fun processClassDeclaration(classDeclaration: KSClassDeclaration): DaggerRepositoryModel? {
  // Extract parent interfaces
  val parentInterfaces = extractParentInterfaces(classDeclaration)

  if (parentInterfaces.isEmpty()) {
    return null
  }

  // Extract Dagger scope annotations
  val scopeInfo = extractDaggerScopeAnnotations(classDeclaration)

  return DaggerRepositoryModel(
    parentsList = parentInterfaces,
    targetClass = classDeclaration,
    singleton = scopeInfo.singleton,
    reusable = scopeInfo.reusable,
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
 * Data class to hold Dagger scope annotation information.
 */
private data class DaggerScopeInfo(
  val singleton: Boolean,
  val reusable: Boolean,
)

/**
 * Extracts Dagger scope annotation information from a class declaration.
 *
 * @param classDeclaration The class declaration to analyze
 * @return [DaggerScopeInfo] containing the Dagger scope annotation flags
 */
private fun extractDaggerScopeAnnotations(classDeclaration: KSClassDeclaration): DaggerScopeInfo =
  DaggerScopeInfo(
    singleton = classDeclaration.annotations.hasAnnotation(DaggerConstants.DAGGER_SINGLETON.simpleName),
    reusable = classDeclaration.annotations.hasAnnotation(DaggerConstants.DAGGER_REUSABLE.simpleName),
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
