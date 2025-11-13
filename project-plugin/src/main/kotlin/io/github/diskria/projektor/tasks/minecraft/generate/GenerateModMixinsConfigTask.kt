package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.*
import io.github.diskria.kotlin.utils.extensions.common.`dot․case`
import io.github.diskria.kotlin.utils.extensions.common.`path∕case`
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.minecraft.configs.mixins.MixinsConfig
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateModMixinsConfigTask : DefaultTask() {

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

    @get:Internal
    abstract val sideSourceSetDirectories: MapProperty<ModSide, File>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }

    @TaskAction
    fun generate() {
        val minecraftMod = minecraftMod.get()
        val sideSourceSetDirectories = sideSourceSetDirectories.get()

        val sideMixins = sideSourceSetDirectories.mapValues {
            val mixinsRoot = it.value.resolve(minecraftMod.packagePath).resolve(ProjectDirectories.MINECRAFT_MIXINS)
            mixinsRoot
                .walkDirectories()
                .flatMap { directory ->
                    val relativePath = directory.relativeTo(mixinsRoot).path
                    directory.listFilesWithExtension(Constants.File.Extension.JAVA).map { javaFile ->
                        val className = javaFile.nameWithoutExtension
                        if (relativePath.isEmpty()) className
                        else relativePath.setCase(`path∕case`, `dot․case`).appendPackageName(className)
                    }
                }
                .toList()
                .sorted()
        }.filterValues { it.isNotEmpty() }

        val config = MixinsConfig.of(minecraftMod, sideMixins)
        config.serializeJsonToFile(outputFile.get().asFile.ensureFileExists())
    }
}
