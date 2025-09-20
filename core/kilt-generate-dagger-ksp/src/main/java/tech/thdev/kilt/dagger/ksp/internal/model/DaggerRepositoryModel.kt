package tech.thdev.kilt.dagger.ksp.internal.model

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName

/**
 * Data model representing a class that needs Dagger module generation.
 *
 * This model encapsulates all the information needed to generate a Dagger module
 * for binding an implementation class to its interfaces with the appropriate scope.
 *
 * @property parentsList List of parent interfaces that this class implements
 * @property targetClass The KSP class declaration of the implementation class
 * @property singleton Whether the class is annotated with @Singleton
 * @property reusable Whether the class is annotated with @Reusable
 */
internal data class DaggerRepositoryModel(
  val parentsList: List<ClassName>,
  val targetClass: KSClassDeclaration,
  val singleton: Boolean,
  val reusable: Boolean,
) {
  /**
   * Validates that the model has at least one parent interface.
   * Classes without interfaces don't need module generation.
   *
   * @return true if the model is valid for module generation
   */
  fun isValidForGeneration(): Boolean =
    parentsList.isNotEmpty()

  /**
   * Determines if the class has any scope annotation.
   *
   * @return true if any scope annotation is present
   */
  fun hasScope(): Boolean =
    singleton || reusable

  /**
   * Gets a human-readable description of the applied scope.
   *
   * @return String description of the scope, or "No scope" if none applied
   */
  fun getScopeDescription(): String = when {
    singleton -> "Singleton"
    reusable -> "Reusable"
    else -> "No scope"
  }

  /**
   * Determines the appropriate scope annotation to use.
   * Priority: Singleton > Reusable > null
   *
   * @return ClassName of the scope annotation or null if no scope
   */
  fun getScopeAnnotation(): ClassName? = when {
    singleton -> tech.thdev.kilt.dagger.ksp.internal.DaggerConstants.DAGGER_SINGLETON
    reusable -> tech.thdev.kilt.dagger.ksp.internal.DaggerConstants.DAGGER_REUSABLE
    else -> null
  }
}