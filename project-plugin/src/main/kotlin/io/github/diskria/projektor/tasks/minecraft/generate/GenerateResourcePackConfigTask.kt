package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.minecraft.configs.packs.resources.ResourcePackConfig
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateResourcePackConfigTask : DefaultTask() {

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

    @get:Input
    abstract val format: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }

    @TaskAction
    fun generate() {
        val minecraftMod = minecraftMod.get()
        val format = format.get()
        val outputFile = outputFile.get().asFile

        val config = ResourcePackConfig.of(minecraftMod, format)
        config.serializeJsonToFile(outputFile.ensureFileExists())
    }
}
