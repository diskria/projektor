package io.github.diskria.projektor.tasks.minecraft.generate

import com.palantir.javapoet.*
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import javax.lang.model.element.Modifier

abstract class GenerateModEntryPointTask : DefaultTask() {

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

    @get:Internal
    abstract val modSide: Property<ModSide>

    @get:Internal
    abstract val outputDirectory: DirectoryProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }

    @Suppress("SpellCheckingInspection")
    @TaskAction
    fun generate() {
        val mod = minecraftMod.get()
        val side = modSide.get()
        val outputDirectory = outputDirectory.get().asFile

        val entryPointClassName = mod.getEntryPointName(side)
        val entryPointBuilder = TypeSpec.classBuilder(entryPointClassName)

        val isClientSide = side == ModSide.CLIENT
        val isDedicatedServerEnvironment = mod.config.environment == ModEnvironment.DEDICATED_SERVER
        val environmentName = when {
            isClientSide -> side.getName(PascalCase)
            isDedicatedServerEnvironment -> mod.config.environment.getName(PascalCase)
            else -> Constants.Char.EMPTY
        }
        val sideName = when {
            isClientSide || isDedicatedServerEnvironment -> side.getName(PascalCase)
            else -> Constants.Char.EMPTY
        }
        val entryPointClass = when (mod.loader.mapToEnum()) {
            ModLoaderType.FABRIC -> {
                val superInterfaceClassName = ClassName.get("net.fabricmc.api", "${environmentName}ModInitializer")
                val methodName = "onInitialize$sideName"
                val initializeMethod = MethodSpec.methodBuilder(methodName)
                    .addAnnotation(Override::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Void.TYPE)
                    .build()
                entryPointBuilder
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(superInterfaceClassName)
                    .addMethod(initializeMethod)
                    .build()
            }

            ModLoaderType.LEGACY_FABRIC -> TODO()

            ModLoaderType.ORNITHE -> {
                entryPointBuilder
                    .addModifiers(Modifier.PUBLIC)
                    .build()
            }

            ModLoaderType.BABRIC -> TODO()

            ModLoaderType.NEOFORGE -> {
                val annotation = AnnotationSpec.builder(ClassName.get("net.neoforged.fml.common", "Mod")).apply {
                    addMember("value", $$"$S", mod.id)
                    if (environmentName.isNotEmpty()) {
                        val distEnumClassName = ClassName.get("net.neoforged.api.distmarker", "Dist")
                        addMember("dist", $$"$T.$${environmentName.uppercase()}", distEnumClassName)
                    }
                }.build()
                entryPointBuilder
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(annotation)
                    .build()
            }

            ModLoaderType.FORGE -> TODO()
        }
        val javaFile = JavaFile.builder(mod.packageName.appendPackageName(side.getName()), entryPointClass).build()
        javaFile.writeTo(outputDirectory)
    }
}
