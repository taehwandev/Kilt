package tech.thdev.kilt.dagger.ksp.internal

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import tech.thdev.kilt.annotations.KiltGenerateModule

/**
 * Constants for Dagger annotation class names used in code generation.
 *
 * This object centralizes all the ClassName references for Dagger-specific annotations
 * without Hilt dependencies, ensuring consistency across the code generation process.
 */
internal object DaggerConstants {

  // region Dagger Core Annotations
  /** Dagger @Module annotation for marking module classes */
  val DAGGER_MODULE = ClassName("dagger", "Module")

  /** Dagger @Binds annotation for binding implementations to interfaces */
  val DAGGER_BINDS = ClassName("dagger", "Binds")

  // region Dagger Scope Annotations
  /** javax.inject @Singleton annotation for singleton scoped dependencies */
  val DAGGER_SINGLETON = ClassName("javax.inject", "Singleton")

  /** Dagger @Reusable annotation for reusable scoped dependencies */
  val DAGGER_REUSABLE = ClassName("dagger", "Reusable")
  // endregion

  // region Kilt Annotations
  /** Kilt @KiltGenerateModule annotation for marking classes for module generation */
  val KILT_GENERATE_MODULE = KiltGenerateModule::class.asClassName()
  // endregion
}
