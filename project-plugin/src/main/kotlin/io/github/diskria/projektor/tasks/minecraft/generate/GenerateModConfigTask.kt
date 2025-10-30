package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.buildPath
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.listFilesWithExtension
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.minecraft.config.FabricModConfig
import io.github.diskria.projektor.minecraft.config.OrnitheModConfig
import io.github.diskria.projektor.minecraft.loaders.*
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateModConfigTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

    @get:InputDirectory
    abstract val sourceSetsRoot: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        metadata.convention(project.getProjektMetadata())
    }

    @TaskAction
    fun generate() {
        val minecraftMod = minecraftMod.get()
        val outputFile = outputFile.get().asFile.ensureFileExists()

        when (minecraftMod.loader) {
            Fabric -> {
                val dataGenerators = sourceSetsRoot.get().asFile
                    .resolve(buildPath("datagen", "kotlin", minecraftMod.packagePath))
                    .listFilesWithExtension("kt")
                    .map { minecraftMod.packageName.appendPackageName(it.nameWithoutExtension) }
                FabricModConfig.of(minecraftMod, dataGenerators).serializeJsonToFile(outputFile)
            }

            Ornithe -> {
                OrnitheModConfig.of(minecraftMod).serializeJsonToFile(outputFile)
            }

            Quilt -> TODO()
            Forge -> TODO()
            NeoForge -> TODO()
        }
    }
}
