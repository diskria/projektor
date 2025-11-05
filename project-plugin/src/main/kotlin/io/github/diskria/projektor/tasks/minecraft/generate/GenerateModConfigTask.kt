package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.kotlin.utils.extensions.common.failWithUnsupportedType
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.kotlin.utils.extensions.serialization.serializeTomlToFile
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.minecraft.loaders.fabric.Fabric
import io.github.diskria.projektor.minecraft.loaders.fabric.config.FabricModConfig
import io.github.diskria.projektor.minecraft.loaders.fabric.ornithe.Ornithe
import io.github.diskria.projektor.minecraft.loaders.fabric.ornithe.config.OrnitheModConfig
import io.github.diskria.projektor.minecraft.loaders.fabric.quilt.Quilt
import io.github.diskria.projektor.minecraft.loaders.forge.Forge
import io.github.diskria.projektor.minecraft.loaders.forge.neoforge.NeoForge
import io.github.diskria.projektor.minecraft.loaders.forge.neoforge.config.NeoforgeModConfig
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateModConfigTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

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
            Fabric -> FabricModConfig.of(minecraftMod).serializeJsonToFile(outputFile)
            Ornithe -> OrnitheModConfig.of(minecraftMod).serializeJsonToFile(outputFile)
            Quilt -> TODO()
            Forge -> TODO()
            NeoForge -> NeoforgeModConfig.of(minecraftMod).serializeTomlToFile(outputFile)
            else -> failWithUnsupportedType(minecraftMod.loader::class)
        }
    }
}
