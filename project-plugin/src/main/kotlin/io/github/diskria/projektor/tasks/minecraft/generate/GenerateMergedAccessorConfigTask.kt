package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.generics.joinByNewLine
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.projektor.ProjektorGradlePlugin
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateMergedAccessorConfigTask : DefaultTask() {

    @get:Input
    abstract val accessorConfigs: ListProperty<File>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }

    @TaskAction
    fun generate() {
        val accessorConfigs = accessorConfigs.get()
        val outputFile = outputFile.get().asFile.ensureFileExists()

        outputFile.writeText(buildFileText(accessorConfigs))
    }

    companion object {
        fun buildFileText(configs: List<File>): String =
            configs
                .mapNotNull { config ->
                    config
                        .readLines()
                        .mapNotNull { line -> line.trim().toNullIfEmpty() }
                        .filterNot { it.startsWith(Constants.Char.NUMBER_SIGN) }
                        .toNullIfEmpty()
                }
                .flatten()
                .toSet()
                .joinByNewLine()
    }
}
