package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.kotlin.utils.extensions.serialization.serializeTomlToFile
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.minecraft.configs.fabric.FabricModConfig
import io.github.diskria.projektor.minecraft.configs.forge.ForgeModConfig
import io.github.diskria.projektor.minecraft.configs.neoforge.NeoForgeModConfig
import io.github.diskria.projektor.minecraft.configs.ornithe.OrnitheModConfig
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateModConfigTask : DefaultTask() {

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

    @get:Internal
    abstract val singleSide: Property<ModSide?>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        singleSide.convention(null)
    }

    @TaskAction
    fun generate() {
        val minecraftMod = minecraftMod.get()
        val singleSide = singleSide.orNull
        val outputFile = outputFile.get().asFile.ensureFileExists()

        when (minecraftMod.loader.mapToEnum()) {
            ModLoaderType.FABRIC -> FabricModConfig.of(minecraftMod, singleSide).serializeJsonToFile(outputFile)
            ModLoaderType.LEGACY_FABRIC -> FabricModConfig.of(minecraftMod, singleSide).serializeJsonToFile(outputFile)
            ModLoaderType.ORNITHE -> OrnitheModConfig.of(minecraftMod, singleSide).serializeJsonToFile(outputFile)
            ModLoaderType.FORGE -> ForgeModConfig.of(minecraftMod).serializeTomlToFile(outputFile)
            ModLoaderType.NEOFORGE -> NeoForgeModConfig.of(minecraftMod).serializeTomlToFile(outputFile)
        }
    }
}
