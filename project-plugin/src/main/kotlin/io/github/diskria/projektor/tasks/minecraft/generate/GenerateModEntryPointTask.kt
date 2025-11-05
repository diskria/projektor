package io.github.diskria.projektor.tasks.minecraft.generate

import com.palantir.javapoet.ClassName
import com.palantir.javapoet.JavaFile
import com.palantir.javapoet.MethodSpec
import com.palantir.javapoet.TypeSpec
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.loaders.fabric.Fabric
import io.github.diskria.projektor.minecraft.loaders.fabric.ornithe.Ornithe
import io.github.diskria.projektor.minecraft.loaders.forge.neoforge.NeoForge
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

    @TaskAction
    fun generate() {
        val mod = minecraftMod.get()
        val side = modSide.get()
        val outputDirectory = outputDirectory.get().asFile

        val entryPointClassName = mod.getEntryPointName(side)

        val entryPointClass = when (mod.loader) {
            Fabric -> {
                val (superInterface, method) = when {
                    side == ModSide.CLIENT -> {
                        "ClientModInitializer" to "onInitializeClient"
                    }

                    mod.config.environment == ModEnvironment.DEDICATED_SERVER_ONLY -> {
                        "DedicatedServerModInitializer" to "onInitializeServer"
                    }

                    else -> {
                        "ModInitializer" to "onInitialize"
                    }
                }
                val modInitializer = ClassName.get("net.fabricmc.api", superInterface)

                val initializeMethod = MethodSpec.methodBuilder(method)
                    .addAnnotation(Override::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Void.TYPE)
                    .build()

                TypeSpec.classBuilder(entryPointClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(modInitializer)
                    .addMethod(initializeMethod)
                    .build()
            }

            Ornithe -> {
                TODO()
            }

            NeoForge -> {
                TODO()
            }

            else -> TODO()
        }
        val javaFile = JavaFile.builder(mod.packageName.appendPackageName(side.getName()), entryPointClass).build()
        javaFile.writeTo(outputDirectory)
    }
}
