package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.`dot․case`
import io.github.diskria.kotlin.utils.extensions.common.`path∕case`
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.listFilesWithExtension
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.minecraft.config.MixinsConfig
import io.github.diskria.projektor.minecraft.getSourceSets
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateModMixinsConfigTask : DefaultTask() {

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

    @get:InputDirectory
    abstract val sourceSetsRoot: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }

    @TaskAction
    fun generate() {
        val minecraftMod = minecraftMod.get()
        val sourceSetsRoot = sourceSetsRoot.get().asFile
        val outputFile = outputFile.get().asFile.ensureFileExists()

        val mixinsBySourceSet = minecraftMod.config.environment.getSourceSets().mapNotNull { sourceSet ->
            val rootDirectory = sourceSetsRoot
                .resolve(sourceSet.getName())
                .resolve("java")
                .resolve(minecraftMod.packagePath)
                .resolve("mixins")
            val mixins = rootDirectory
                .walkTopDown()
                .filter { it.isDirectory && !it.isHidden }
                .mapNotNull { directory ->
                    val files = directory.listFilesWithExtension("java").toNullIfEmpty() ?: return@mapNotNull null
                    val directoryPath = directory.relativeTo(rootDirectory).path
                    val fileNames = files.map { it.nameWithoutExtension }.sorted()
                    directoryPath to fileNames
                }
                .sortedBy { it.first }
                .toList()
                .flatMap { (directoryPath, fileNames) ->
                    fileNames.map { fileName ->
                        if (directoryPath.isEmpty()) fileName
                        else directoryPath.setCase(`path∕case`, `dot․case`).appendPackageName(fileName)
                    }
                }
                .toNullIfEmpty() ?: return@mapNotNull null
            sourceSet to mixins
        }.toMap()

        val config = MixinsConfig.of(minecraftMod, mixinsBySourceSet)
        config.serializeJsonToFile(outputFile)
    }
}
