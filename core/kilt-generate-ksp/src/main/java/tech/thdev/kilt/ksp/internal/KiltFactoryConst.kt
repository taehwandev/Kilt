package tech.thdev.kilt.ksp.internal

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import tech.thdev.kilt.annotations.KiltGenerateModule

internal object KiltFactoryConst {

    // Dagger
    val DAGGER_MODULE = ClassName("dagger", "Module")
    val DAGGER_BINDS = ClassName("dagger", "Binds")
    val DAGGER_SINGLETON = ClassName("javax.inject", "Singleton")
    val DAGGER_REUSABLE = ClassName("dagger", "Reusable")
    val DAGGER_ACTIVITY_RETAINED_SCOPED = ClassName("dagger.hilt.android.scopes", "ActivityRetainedScoped")
    val DAGGER_VIEW_MODEL_SCOPED = ClassName("dagger.hilt.android.scopes", "ViewModelScoped")

    // Hilt Scopes & Components
    val DAGGER_INSTALL_IN = ClassName("dagger.hilt", "InstallIn")
    val SINGLETON_COMPONENT = ClassName("dagger.hilt.components", "SingletonComponent")
    val ACTIVITY_RETAINED_COMPONENT = ClassName("dagger.hilt.android.components", "ActivityRetainedComponent")
    val VIEW_MODEL_COMPONENT = ClassName("dagger.hilt.android.components", "ViewModelComponent")
    val ACTIVITY_COMPONENT = ClassName("dagger.hilt.android.components", "ActivityComponent")

    // Kilt Annotation
    val KILT_GENERATE_MODULE = KiltGenerateModule::class.asClassName()
}
