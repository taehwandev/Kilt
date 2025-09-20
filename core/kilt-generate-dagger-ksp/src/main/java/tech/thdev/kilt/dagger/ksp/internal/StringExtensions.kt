package tech.thdev.kilt.dagger.ksp.internal

/**
 * Extension functions for string manipulation in Dagger module generation.
 * These functions provide consistent naming conventions for generated code.
 */

/**
 * Adds "bind" prefix to create a Dagger @Binds method name.
 *
 * Example: "UserRepository" becomes "bindUserRepository"
 *
 * @return String with "bind" prefix added
 */
internal fun String.mergePrefixBind(): String =
  "bind$this"

/**
 * Adds "Module" suffix to create a Dagger module class name.
 *
 * Example: "UserRepositoryImpl" becomes "UserRepositoryImplModule"
 *
 * @return String with "Module" suffix added
 */
internal fun String.mergeSuffixDaggerModule(): String =
  "${this}Module"

/**
 * Removes "Impl" suffix from implementation class names.
 * This is used to create cleaner parameter names in generated methods.
 *
 * Example: "UserRepositoryImpl" becomes "UserRepository"
 *
 * @return String with "Impl" suffix removed, or original string if suffix not found
 */
internal fun String.removeSuffixImpl(): String =
  this.removeSuffix("Impl")
