package tech.thdev.kilt.hilt.ksp.internal.model

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
 * @property activityRetained Whether the class is annotated with @ActivityRetainedScoped
 * @property viewModel Whether the class is annotated with @ViewModelScoped
 */
internal data class ResearchRepositoryModel(
  val parentsList: List<ClassName>,
  val targetClass: KSClassDeclaration,
  val singleton: Boolean,
  val activityRetained: Boolean,
  val viewModel: Boolean,
) {
  /**
   * Validates that the model has at least one parent interface.
   * Classes without interfaces don't need module generation.
   *
   * @return true if the model is valid for module generation
   */
  fun isValidForGeneration(): Boolean = parentsList.isNotEmpty()

  /**
   * Determines if the class has any scope annotation.
   *
   * @return true if any scope annotation is present
   */
  fun hasScope(): Boolean = singleton || activityRetained || viewModel

  /**
   * Gets a human-readable description of the applied scope.
   *
   * @return String description of the scope, or "No scope" if none applied
   */
  fun getScopeDescription(): String = when {
    singleton -> "Singleton"
    activityRetained -> "ActivityRetained"
    viewModel -> "ViewModel"
    else -> "No scope"
  }
}
