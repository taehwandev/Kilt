//package tech.thdev.kilt.ksp
//
//import com.tschuchort.compiletesting.KotlinCompilation
//import com.tschuchort.compiletesting.SourceFile
//import com.tschuchort.compiletesting.kspArgs
//import com.tschuchort.compiletesting.symbolProcessorProviders
//import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
//import org.junit.Assert.assertEquals
//import org.junit.Rule
//import org.junit.Test
//import org.junit.rules.TemporaryFolder
//import java.io.File
//
//@OptIn(ExperimentalCompilerApi::class)
//internal class KiltFactoryProcessorProviderTest {
//
//    @get:Rule
//    val tempFolder = TemporaryFolder()
//
//    // KSP 테스트에 필요한 모든 어노테이션과 기본 클래스를 미리 정의합니다.
//    private val annotationSourceFiles = SourceFile.kotlin(
//        "Annotations.kt",
//        """
//            package tech.thdev.kilt.annotations
//            annotation class KiltGenerateModule
//        """.trimIndent()
//    )
//
//    @Test
//    fun `test generate module with @Singleton`() {
//        // Given
//        val repositoryInterface = SourceFile.kotlin(
//            "SampleRepository.kt",
//            """
//                package com.example.repository
//                interface SampleRepository
//            """.trimIndent()
//        )
//        val repositoryImpl = SourceFile.kotlin(
//            "SampleRepositoryImpl.kt",
//            """
//                package com.example.repository.impl
//                import com.example.repository.SampleRepository
//                import tech.thdev.kilt.annotations.KiltGenerateModule
//                import javax.inject.Inject
//                import javax.inject.Singleton
//
//                @Singleton
//                @KiltGenerateModule
//                class SampleRepositoryImpl @Inject constructor() : SampleRepository
//            """.trimIndent()
//        )
//
//        // When
//        val result = compile(annotationSourceFiles, repositoryInterface, repositoryImpl)
//
//        // Then
//        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
//        val expectedSource = """
//            package com.example.repository.impl.dagger
//
//            import com.example.repository.SampleRepository
//            import com.example.repository.impl.SampleRepositoryImpl
//            import dagger.Binds
//            import dagger.Module
//            import dagger.Reusable
//            import dagger.hilt.InstallIn
//            import dagger.hilt.components.SingletonComponent
//
//            @Module
//            @InstallIn(SingletonComponent::class)
//            public interface SampleRepositoryImplModule {
//              @Binds
//              @Reusable
//              public fun bindSampleRepository(sampleRepositoryImpl: SampleRepositoryImpl): SampleRepository
//            }
//        """.trimIndent()
//        assertEquals(expectedSource, result.sourceFor("SampleRepositoryImplModule.kt"))
//    }
//
////    @Test
////    fun `test generate module with @ActivityRetainedScoped`() {
////        // Given
////        val repositoryInterface = SourceFile.kotlin(
////            "SessionRepository.kt",
////            """
////                package com.example.repository
////                interface SessionRepository
////            """.trimIndent()
////        )
////        val repositoryImpl = SourceFile.kotlin(
////            "SessionRepositoryImpl.kt",
////            """
////                package com.example.repository.impl
////                import com.example.repository.SessionRepository
////                import tech.thdev.kilt.annotations.KiltGenerateModule
////                import javax.inject.Inject
////                import dagger.hilt.android.scopes.ActivityRetainedScoped
////
////                @ActivityRetainedScoped
////                @KiltGenerateModule
////                class SessionRepositoryImpl @Inject constructor() : SessionRepository
////            """.trimIndent()
////        )
////
////        // When
////        val result = compile(repositoryInterface, repositoryImpl)
////
////        // Then
////        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
////        val expectedSource = """
////            package com.example.repository.impl.dagger
////
////            import com.example.repository.SessionRepository
////            import com.example.repository.impl.SessionRepositoryImpl
////            import dagger.Binds
////            import dagger.Module
////            import dagger.hilt.InstallIn
////            import dagger.hilt.android.components.ActivityRetainedComponent
////            import dagger.hilt.android.scopes.ActivityRetainedScoped
////
////            @Module
////            @InstallIn(ActivityRetainedComponent::class)
////            public interface SessionRepositoryImplModule {
////              @Binds
////              @ActivityRetainedScoped
////              public fun bindSessionRepository(sessionRepositoryImpl: SessionRepositoryImpl): SessionRepository
////            }
////        """.trimIndent()
////        assertEquals(expectedSource, result.sourceFor("SessionRepositoryImplModule.kt"))
////    }
////
////    @Test
////    fun `test generate module with @ViewModelScoped`() {
////        // Given
////        val repositoryInterface = SourceFile.kotlin(
////            "UserPreferenceRepository.kt",
////            """
////                package com.example.repository
////                interface UserPreferenceRepository
////            """.trimIndent()
////        )
////        val repositoryImpl = SourceFile.kotlin(
////            "UserPreferenceRepositoryImpl.kt",
////            """
////                package com.example.repository.impl
////                import com.example.repository.UserPreferenceRepository
////                import tech.thdev.kilt.annotations.KiltGenerateModule
////                import javax.inject.Inject
////                import dagger.hilt.android.scopes.ViewModelScoped
////
////                @ViewModelScoped
////                @KiltGenerateModule
////                class UserPreferenceRepositoryImpl @Inject constructor() : UserPreferenceRepository
////            """.trimIndent()
////        )
////
////        // When
////        val result = compile(repositoryInterface, repositoryImpl)
////
////        // Then
////        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
////        val expectedSource = """
////            package com.example.repository.impl.dagger
////
////            import com.example.repository.UserPreferenceRepository
////            import com.example.repository.impl.UserPreferenceRepositoryImpl
////            import dagger.Binds
////            import dagger.Module
////            import dagger.hilt.InstallIn
////            import dagger.hilt.android.components.ViewModelComponent
////            import dagger.hilt.android.scopes.ViewModelScoped
////
////            @Module
////            @InstallIn(ViewModelComponent::class)
////            public interface UserPreferenceRepositoryImplModule {
////              @Binds
////              @ViewModelScoped
////              public fun bindUserPreferenceRepository(userPreferenceRepositoryImpl: UserPreferenceRepositoryImpl): UserPreferenceRepository
////            }
////        """.trimIndent()
////        assertEquals(expectedSource, result.sourceFor("UserPreferenceRepositoryImplModule.kt"))
////    }
////
////    @Test
////    fun `test generate module with no scope defaults to ActivityComponent`() {
////        // Given
////        val repositoryInterface = SourceFile.kotlin(
////            "LoginRepository.kt",
////            """
////                package com.example.repository
////                interface LoginRepository
////            """.trimIndent()
////        )
////        val repositoryImpl = SourceFile.kotlin(
////            "LoginRepositoryImpl.kt",
////            """
////                package com.example.repository.impl
////                import com.example.repository.LoginRepository
////                import tech.thdev.kilt.annotations.KiltGenerateModule
////                import javax.inject.Inject
////
////                @KiltGenerateModule
////                class LoginRepositoryImpl @Inject constructor() : LoginRepository
////            """.trimIndent()
////        )
////
////        // When
////        val result = compile(repositoryInterface, repositoryImpl)
////
////        // Then
////        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
////        val expectedSource = """
////            package com.example.repository.impl.dagger
////
////            import com.example.repository.LoginRepository
////            import com.example.repository.impl.LoginRepositoryImpl
////            import dagger.Binds
////            import dagger.Module
////            import dagger.hilt.InstallIn
////            import dagger.hilt.android.components.ActivityComponent
////
////            @Module
////            @InstallIn(ActivityComponent::class)
////            public interface LoginRepositoryImplModule {
////              @Binds
////              public fun bindLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository
////            }
////        """.trimIndent()
////        assertEquals(expectedSource, result.sourceFor("LoginRepositoryImplModule.kt"))
////    }
////
////    @Test
////    fun `test generate module for class with multiple interfaces`() {
////        // Given
////        val interfaces = SourceFile.kotlin(
////            "AuthInterfaces.kt",
////            """
////                package com.example.auth
////                interface AuthRepository
////                interface TokenManager
////            """.trimIndent()
////        )
////        val repositoryImpl = SourceFile.kotlin(
////            "AuthRepositoryImpl.kt",
////            """
////                package com.example.auth.impl
////                import com.example.auth.AuthRepository
////                import com.example.auth.TokenManager
////                import tech.thdev.kilt.annotations.KiltGenerateModule
////                import javax.inject.Inject
////                import javax.inject.Singleton
////
////                @Singleton
////                @KiltGenerateModule
////                class AuthRepositoryImpl @Inject constructor() : AuthRepository, TokenManager
////            """.trimIndent()
////        )
////
////        // When
////        val result = compile(interfaces, repositoryImpl)
////
////        // Then
////        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
////        val expectedSource = """
////            package com.example.auth.impl.dagger
////
////            import com.example.auth.AuthRepository
////            import com.example.auth.TokenManager
////            import com.example.auth.impl.AuthRepositoryImpl
////            import dagger.Binds
////            import dagger.Module
////            import dagger.Reusable
////            import dagger.hilt.InstallIn
////            import dagger.hilt.components.SingletonComponent
////
////            @Module
////            @InstallIn(SingletonComponent::class)
////            public interface AuthRepositoryImplModule {
////              @Binds
////              @Reusable
////              public fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
////
////              @Binds
////              @Reusable
////              public fun bindTokenManager(authRepositoryImpl: AuthRepositoryImpl): TokenManager
////            }
////        """.trimIndent()
////        assertEquals(expectedSource, result.sourceFor("AuthRepositoryImplModule.kt"))
////    }
////
////    @Test
////    fun `test does not generate module for class with no interfaces`() {
////        // Given
////        val noInterfaceClass = SourceFile.kotlin(
////            "AnalyticsLogger.kt",
////            """
////                package com.example.analytics
////                import tech.thdev.kilt.annotations.KiltGenerateModule
////                import javax.inject.Inject
////
////                @KiltGenerateModule
////                class AnalyticsLogger @Inject constructor()
////            """.trimIndent()
////        )
////
////        // When
////        val result = compile(noInterfaceClass)
////
////        // Then
////        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
////        try {
////            result.sourceFor("AnalyticsLoggerModule.kt")
////            fail("AnalyticsLoggerModule.kt should not have been generated as AnalyticsLogger implements no interfaces.")
////        } catch (e: IllegalArgumentException) {
////            // This is expected.
////        }
////    }
//
//    // --- Helper Functions ---
//
//    private fun compile(vararg sourceFiles: SourceFile): KotlinCompilation.Result {
//        return KotlinCompilation().apply {
//            sources = sourceFiles.toList() // 모든 테스트에 기본 어노테이션 소스를 추가
//            symbolProcessorProviders = listOf(KiltFactoryProcessorProvider())
//            kspArgs = mutableMapOf("isTest" to "Y")
//            workingDir = tempFolder.root
//            inheritClassPath = true
//            verbose = false
//        }.compile()
//    }
//
//    private fun KotlinCompilation.Result.sourceFor(fileName: String): String {
//        val sources = kspGeneratedSources()
//        val sourceFile = sources.find { it.name == fileName }
//            ?: throw IllegalArgumentException("Could not find file '$fileName' in generated sources: ${sources.map { it.name }}")
//        return sourceFile.readText().trim()
//    }
//
//    private fun KotlinCompilation.Result.kspGeneratedSources(): List<File> {
//        val kspWorkingDir = outputDirectory.parentFile.resolve("ksp")
//        val kspGeneratedDir = kspWorkingDir.resolve("sources")
//        val kotlinGeneratedDir = kspGeneratedDir.resolve("kotlin")
//        val javaGeneratedDir = kspGeneratedDir.resolve("java")
//        return kotlinGeneratedDir.walk().filter { it.isFile }.toList() +
//                javaGeneratedDir.walk().filter { it.isFile }.toList()
//    }
//}
