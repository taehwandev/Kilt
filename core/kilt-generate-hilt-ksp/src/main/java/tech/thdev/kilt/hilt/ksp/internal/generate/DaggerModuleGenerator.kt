package tech.thdev.kilt.hilt.ksp.internal.generate

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import tech.thdev.kilt.hilt.ksp.internal.KiltFactoryConst
import tech.thdev.kilt.hilt.ksp.internal.mergePrefixBind
import tech.thdev.kilt.hilt.ksp.internal.mergeSuffixDaggerModule
import tech.thdev.kilt.hilt.ksp.internal.model.ResearchRepositoryModel
import tech.thdev.kilt.hilt.ksp.internal.removeSuffixImpl
import java.io.OutputStreamWriter

/**
 * Generates Dagger modules for dependency injection binding.
 *
 * This function creates a Dagger module interface that binds implementation classes
 * to their parent interfaces with the appropriate scope and component installation.
 *
 * Generated modules follow the pattern:
 * - Package: {original.package}.dagger
 * - Class name: {ImplementationClass}Module
 * - Methods: bind{InterfaceName}({parameterName}: {ImplementationClass}): {Interface}
 *
 * @param codeGenerator The KSP code generator for creating new files
 * @param model The model containing class information for module generation
 * @throws Exception if code generation fails
 */
internal fun generateDaggerModule(
  codeGenerator: CodeGenerator,
  model: ResearchRepositoryModel,
) {
  val targetClass = model.targetClass
  val targetClassName = ClassName(
    targetClass.packageName.asString(),
    targetClass.simpleName.getShortName()
  )
  val moduleInfo = createModuleInfo(targetClassName)
  val componentInfo = determineComponentAndScope(model)

  try {
    val fileSpec = buildModuleFileSpec(
      moduleInfo = moduleInfo,
      componentInfo = componentInfo,
      model = model,
      targetClassName = targetClassName
    )

    writeGeneratedFile(
      codeGenerator = codeGenerator,
      fileSpec = fileSpec,
      moduleInfo = moduleInfo,
      targetClass = targetClass
    )
  } catch (e: Exception) {
    throw Exception("Failed to generate Dagger module for ${targetClassName.simpleName}", e)
  }
}

/**
 * Data class containing module generation information.
 */
private data class ModuleInfo(
  val fileName: String,
  val packageName: String,
)

/**
 * Data class containing component and scope information.
 */
private data class ComponentInfo(
  val hiltComponent: ClassName,
  val scopeAnnotation: ClassName?,
)

/**
 * Creates module information based on the target class.
 */
private fun createModuleInfo(targetClassName: ClassName): ModuleInfo =
  ModuleInfo(
    fileName = targetClassName.simpleName.mergeSuffixDaggerModule(),
    packageName = "${targetClassName.packageName}.dagger"
  )

/**
 * Determines the appropriate Hilt component and scope annotation based on the model.
 */
private fun determineComponentAndScope(model: ResearchRepositoryModel): ComponentInfo =
  when {
    model.singleton -> ComponentInfo(
      hiltComponent = KiltFactoryConst.SINGLETON_COMPONENT,
      scopeAnnotation = KiltFactoryConst.DAGGER_SINGLETON
    )

    model.activityRetained -> ComponentInfo(
      hiltComponent = KiltFactoryConst.ACTIVITY_RETAINED_COMPONENT,
      scopeAnnotation = KiltFactoryConst.DAGGER_ACTIVITY_RETAINED_SCOPED
    )

    model.viewModel -> ComponentInfo(
      hiltComponent = KiltFactoryConst.VIEW_MODEL_COMPONENT,
      scopeAnnotation = KiltFactoryConst.DAGGER_VIEW_MODEL_SCOPED
    )

    else -> ComponentInfo(
      hiltComponent = KiltFactoryConst.ACTIVITY_COMPONENT,
      scopeAnnotation = null
    )
  }

/**
 * Builds the complete FileSpec for the generated Dagger module.
 */
private fun buildModuleFileSpec(
  moduleInfo: ModuleInfo,
  componentInfo: ComponentInfo,
  model: ResearchRepositoryModel,
  targetClassName: ClassName,
): FileSpec {
  val fileSpecBuilder = FileSpec.builder(moduleInfo.packageName, moduleInfo.fileName)

  val moduleInterface = createModuleInterface(
    moduleName = moduleInfo.fileName,
    componentInfo = componentInfo,
    model = model,
    targetClassName = targetClassName
  )

  return fileSpecBuilder.addType(moduleInterface).build()
}

/**
 * Creates the Dagger module interface with all binding methods.
 */
private fun createModuleInterface(
  moduleName: String,
  componentInfo: ComponentInfo,
  model: ResearchRepositoryModel,
  targetClassName: ClassName,
): TypeSpec {
  val moduleInterfaceBuilder = TypeSpec.interfaceBuilder(moduleName)
    .addModifiers(KModifier.PUBLIC)
    .addAnnotation(KiltFactoryConst.DAGGER_MODULE)
    .addAnnotation(createInstallInAnnotation(componentInfo.hiltComponent))

  // Add binding methods for each parent interface
  model.parentsList.forEach { parentInterface ->
    val bindingMethod = createBindingMethod(
      parentInterface = parentInterface,
      targetClassName = targetClassName,
      scopeAnnotation = componentInfo.scopeAnnotation
    )
    moduleInterfaceBuilder.addFunction(bindingMethod)
  }

  return moduleInterfaceBuilder.build()
}

/**
 * Creates the @InstallIn annotation for the module.
 */
private fun createInstallInAnnotation(hiltComponent: ClassName): AnnotationSpec =
  AnnotationSpec.builder(KiltFactoryConst.DAGGER_INSTALL_IN)
    .addMember("%T::class", hiltComponent)
    .build()

/**
 * Creates a binding method for a specific parent interface.
 */
private fun createBindingMethod(
  parentInterface: ClassName,
  targetClassName: ClassName,
  scopeAnnotation: ClassName?,
): FunSpec {
  val methodBuilder = FunSpec.builder(parentInterface.simpleName.mergePrefixBind())
    .addModifiers(KModifier.ABSTRACT)
    .addAnnotation(KiltFactoryConst.DAGGER_BINDS)
    .addParameter(createMethodParameter(targetClassName))
    .returns(parentInterface)

  // Add scope annotation if present
  scopeAnnotation?.let { methodBuilder.addAnnotation(it) }

  return methodBuilder.build()
}

/**
 * Creates a parameter specification for the binding method.
 */
private fun createMethodParameter(targetClassName: ClassName): ParameterSpec =
  ParameterSpec.builder(
    name = targetClassName.simpleName
      .replaceFirstChar { it.lowercase() }
      .removeSuffixImpl(),
    type = targetClassName
  ).build()

/**
 * Writes the generated file using the code generator.
 */
private fun writeGeneratedFile(
  codeGenerator: CodeGenerator,
  fileSpec: FileSpec,
  moduleInfo: ModuleInfo,
  targetClass: com.google.devtools.ksp.symbol.KSClassDeclaration,
) {
  val file = codeGenerator.createNewFile(
    dependencies = Dependencies(true, targetClass.containingFile!!),
    packageName = moduleInfo.packageName,
    fileName = moduleInfo.fileName
  )

  OutputStreamWriter(file, "UTF-8").use { writer ->
    fileSpec.writeTo(writer)
  }
}
