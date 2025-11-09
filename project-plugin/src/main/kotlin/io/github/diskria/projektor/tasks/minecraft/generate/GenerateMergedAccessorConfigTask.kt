package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.generics.joinByNewLine
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateMergedAccessorConfigTask : DefaultTask() {

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

    @get:Internal
    abstract val sideResourcesDirectories: ListProperty<File>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }

    @TaskAction
    fun generate() {
        val minecraftMod = minecraftMod.get()
        val sideResourcesDirectories = sideResourcesDirectories.get()
        val outputFile = outputFile.get().asFile.ensureFileExists()

        outputFile.writeText(
            sideResourcesDirectories.mapNotNull { resourcesDirectory ->
                resourcesDirectory
                    .resolve(minecraftMod.accessorConfigFileName)
                    .readLines()
                    .mapNotNull { it.trim().toNullIfEmpty() }
                    .toNullIfEmpty()
            }.flatten().toSet().joinByNewLine()
        )
    }
}
