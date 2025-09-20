package tech.thdev.kilt.annotations

/**
 * Annotation to automatically generate Dagger Hilt modules for dependency injection binding.
 *
 * This annotation should be applied to implementation classes that need to be bound to their interfaces.
 * The processor will automatically detect the scope annotations (@Singleton, @ActivityRetainedScoped, etc.)
 * and generate the appropriate Dagger module with the correct scope and component installation.
 *
 * Example usage:
 * ```kotlin
 * @Singleton
 * @KiltGenerateModule
 * class UserRepositoryImpl @Inject constructor() : UserRepository
 * ```
 *
 * This will generate a Dagger module that binds UserRepositoryImpl to UserRepository interface
 * with Singleton scope installed in SingletonComponent.
 *
 * @since 25.9.0
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class KiltGenerateModule
