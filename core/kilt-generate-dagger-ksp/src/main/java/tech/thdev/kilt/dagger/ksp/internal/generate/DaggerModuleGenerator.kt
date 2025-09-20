package tech.thdev.kilt.dagger.ksp.internal.generate

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import tech.thdev.kilt.dagger.ksp.internal.DaggerConstants
import tech.thdev.kilt.dagger.ksp.internal.mergePrefixBind
import tech.thdev.kilt.dagger.ksp.internal.mergeSuffixDaggerModule
import tech.thdev.kilt.dagger.ksp.internal.model.DaggerRepositoryModel
import tech.thdev.kilt.dagger.ksp.internal.removeSuffixImpl
import java.io.OutputStreamWriter

/**
 * Generates standard Dagger modules for dependency injection binding.
 *
 * This function creates a Dagger module interface that binds implementation classes
 * to their parent interfaces with the appropriate scope annotations.
 *
 * Generated modules follow the pattern:
 * - Package: {original.package}.dagger
 * - Class name: {ImplementationClass}Module
 * - Methods: bind{InterfaceName}({parameterName}: {ImplementationClass}): {Interface}
 *
 * Scope handling:
 * - No scope annotation: Standard Dagger @Module
 * - @Singleton: Module with @Singleton scoped @Binds methods
 * - @Reusable: Module with @Reusable scoped @Binds methods
 *
 * @param codeGenerator The KSP code generator for creating new files
 * @param model The model containing class information for module generation
 * @throws Exception if code generation fails
 */
internal fun generateDaggerModule(
  codeGenerator: CodeGenerator,
  model: DaggerRepositoryModel,
) {
  val targetClass = model.targetClass
  val targetClassName = ClassName(
    targetClass.packageName.asString(),
    targetClass.simpleName.getShortName()
  )
  val moduleInfo = createModuleInfo(targetClassName)

  try {
    val fileSpec = buildModuleFileSpec(
      moduleInfo = moduleInfo,
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
 * Creates module information based on the target class.
 */
private fun createModuleInfo(targetClassName: ClassName): ModuleInfo =
  ModuleInfo(
    fileName = targetClassName.simpleName.mergeSuffixDaggerModule(),
    packageName = "${targetClassName.packageName}.dagger"
  )

/**
 * Builds the complete FileSpec for the generated Dagger module.
 */
private fun buildModuleFileSpec(
  moduleInfo: ModuleInfo,
  model: DaggerRepositoryModel,
  targetClassName: ClassName,
): FileSpec {
  val fileSpecBuilder = FileSpec.builder(moduleInfo.packageName, moduleInfo.fileName)

  val moduleInterface = createModuleInterface(
    moduleName = moduleInfo.fileName,
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
  model: DaggerRepositoryModel,
  targetClassName: ClassName,
): TypeSpec {
  val moduleInterfaceBuilder = TypeSpec.interfaceBuilder(moduleName)
    .addModifiers(KModifier.PUBLIC)
    .addAnnotation(DaggerConstants.DAGGER_MODULE)

  // Add binding methods for each parent interface
  model.parentsList.forEach { parentInterface ->
    val bindingMethod = createBindingMethod(
      parentInterface = parentInterface,
      targetClassName = targetClassName,
      scopeAnnotation = model.getScopeAnnotation()
    )
    moduleInterfaceBuilder.addFunction(bindingMethod)
  }

  return moduleInterfaceBuilder.build()
}

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
    .addAnnotation(DaggerConstants.DAGGER_BINDS)
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
  targetClass: KSClassDeclaration,
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
