package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.gradle.utils.extensions.children
import io.github.diskria.gradle.utils.extensions.javaSourcesDirectory
import io.github.diskria.gradle.utils.extensions.sourceSets
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.`dot․case`
import io.github.diskria.kotlin.utils.extensions.common.`path∕case`
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.listFilesWithExtension
import io.github.diskria.kotlin.utils.extensions.mappers.toEnumOrNull
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.extensions.mixins
import io.github.diskria.projektor.minecraft.mixins.MixinsConfig
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
    abstract val sideProjectMixinSourceDirectories: MapProperty<ModSide, File>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        sideProjectMixinSourceDirectories.convention(
            project
                .children
                .mapNotNull { it.name.toEnumOrNull<ModSide>()?.to(it) }
                .toMap()
                .mapValues { it.value.sourceSets.mixins.javaSourcesDirectory }
        )
    }

    @TaskAction
    fun generate() {
        val minecraftMod = minecraftMod.get()
        val sideProjectMixinSourceDirectories = sideProjectMixinSourceDirectories.get()

        val sideMixins = sideProjectMixinSourceDirectories.mapValues {
            val mixinsRoot = it.value.resolve(minecraftMod.packagePath).resolve(ProjectDirectories.MINECRAFT_MIXINS)
            mixinsRoot
                .walkTopDown()
                .filter { file -> file.isDirectory && !file.isHidden }
                .flatMap { directory ->
                    val relativePath = directory.relativeTo(mixinsRoot).path
                    directory
                        .listFilesWithExtension(Constants.File.Extension.JAVA)
                        .map { javaFile -> javaFile.nameWithoutExtension }
                        .map { className ->
                            println("AAAA $className")
                            if (relativePath.isEmpty()) className
                            else relativePath.setCase(`path∕case`, `dot․case`).appendPackageName(className)
                        }
                }
                .toList()
                .sorted()
        }.filterValues { it.isNotEmpty() }
        println(sideMixins)

        val config = MixinsConfig.of(minecraftMod, sideMixins)
        config.serializeJsonToFile(outputFile.get().asFile.ensureFileExists())
    }
}
