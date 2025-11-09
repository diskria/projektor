package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.minecraft.configs.packs.resources.ResourcePackConfig
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateResourcePackConfigTask : DefaultTask() {

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Internal
    abstract val minFormat: Property<String>

    @get:Internal
    abstract val maxFormat: Property<String>

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }

    @TaskAction
    fun generate() {
        val config = ResourcePackConfig.of(minecraftMod.get(), minFormat.get(), maxFormat.get())
        config.serializeJsonToFile(outputFile.get().asFile.ensureFileExists())
    }
}
