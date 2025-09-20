package tech.thdev.kilt.hilt.ksp.internal

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import tech.thdev.kilt.annotations.KiltGenerateModule

/**
 * Constants for Dagger/Hilt and Kilt annotation class names used in code generation.
 *
 * This object centralizes all the ClassName references to ensure consistency
 * across the code generation process and make it easier to update dependencies.
 */
internal object KiltFactoryConst {

  // region Dagger Core Annotations
  /** Dagger @Module annotation for marking module classes */
  val DAGGER_MODULE = ClassName("dagger", "Module")

  /** Dagger @Binds annotation for binding implementations to interfaces */
  val DAGGER_BINDS = ClassName("dagger", "Binds")
  // endregion

  // region Dagger Scope Annotations
  /** javax.inject @Singleton annotation for singleton scoped dependencies */
  val DAGGER_SINGLETON = ClassName("javax.inject", "Singleton")

  /** Dagger @Reusable annotation for reusable scoped dependencies */
  val DAGGER_REUSABLE = ClassName("dagger", "Reusable")

  /** Hilt @ActivityRetainedScoped annotation for activity retained scope */
  val DAGGER_ACTIVITY_RETAINED_SCOPED = ClassName("dagger.hilt.android.scopes", "ActivityRetainedScoped")

  /** Hilt @ViewModelScoped annotation for ViewModel scope */
  val DAGGER_VIEW_MODEL_SCOPED = ClassName("dagger.hilt.android.scopes", "ViewModelScoped")
  // endregion

  // region Hilt Component Installation
  /** Hilt @InstallIn annotation for specifying component installation */
  val DAGGER_INSTALL_IN = ClassName("dagger.hilt", "InstallIn")
  // endregion

  // region Hilt Components
  /** Hilt SingletonComponent for application-wide dependencies */
  val SINGLETON_COMPONENT = ClassName("dagger.hilt.components", "SingletonComponent")

  /** Hilt ActivityRetainedComponent for activity retained dependencies */
  val ACTIVITY_RETAINED_COMPONENT = ClassName("dagger.hilt.android.components", "ActivityRetainedComponent")

  /** Hilt ViewModelComponent for ViewModel dependencies */
  val VIEW_MODEL_COMPONENT = ClassName("dagger.hilt.android.components", "ViewModelComponent")

  /** Hilt ActivityComponent for activity scoped dependencies */
  val ACTIVITY_COMPONENT = ClassName("dagger.hilt.android.components", "ActivityComponent")
  // endregion

  // region Kilt Annotations
  /** Kilt @KiltGenerateModule annotation for marking classes for module generation */
  val KILT_GENERATE_MODULE = KiltGenerateModule::class.asClassName()
  // endregion
}
