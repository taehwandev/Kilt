package tech.thdev.kilt.ksp.internal.model

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName

internal data class ResearchRepositoryModel(
    val parentsList: List<ClassName>,
    val targetClass: KSClassDeclaration,
    val singleton: Boolean,
    val activityRetained: Boolean,
    val viewModel: Boolean,
)
