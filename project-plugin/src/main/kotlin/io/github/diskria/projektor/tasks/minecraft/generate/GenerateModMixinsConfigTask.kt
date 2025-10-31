package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.gradle.utils.extensions.getDirectory
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.`dot․case`
import io.github.diskria.kotlin.utils.extensions.common.`path∕case`
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.listFilesWithExtension
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.mappers.toEnumOrNull
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.extensions.children
import io.github.diskria.projektor.minecraft.config.MixinsConfig
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateModMixinsConfigTask : DefaultTask() {

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

    @get:Internal
    abstract val sideProjectDirectories: MapProperty<ModSide, Directory>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        sideProjectDirectories.convention(
            project.children()
                .mapNotNull { it.name.toEnumOrNull<ModSide>() }
                .associateWith { project.getDirectory(it.getName()) }
        )
    }

    @TaskAction
    fun generate() {
        val minecraftMod = minecraftMod.get()
        val sideProjectDirectories = sideProjectDirectories.get()
        val outputFile = outputFile.get().asFile.ensureFileExists()

        val sideMixins = sideProjectDirectories.mapValues { (_, directory) ->
            val mixinsRoot = directory.asFile.resolve("src/main").resolve("java/${minecraftMod.packagePath}/mixins")
            mixinsRoot
                .walkTopDown()
                .filter { it.isDirectory && !it.isHidden }
                .flatMap { directory ->
                    val relativePath = directory.relativeTo(mixinsRoot).path
                    directory.listFilesWithExtension("java").map {
                        if (relativePath.isEmpty()) it.nameWithoutExtension
                        else relativePath.setCase(`path∕case`, `dot․case`).appendPackageName(it.nameWithoutExtension)
                    }
                }
                .toList()
                .sorted()
        }.filterValues { it.isNotEmpty() }

        val config = MixinsConfig.of(minecraftMod, sideMixins)
        config.serializeJsonToFile(outputFile)
    }
}
