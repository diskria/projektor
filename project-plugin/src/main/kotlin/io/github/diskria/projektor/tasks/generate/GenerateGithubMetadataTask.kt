package io.github.diskria.projektor.tasks.generate

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.serialization.serialize
import io.github.diskria.projektor.projekt.metadata.GithubMetadata
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateGithubMetadataTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<GithubMetadata>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val outputFile = outputFile.get().asFile
        outputFile.parentFile.mkdirs()
        metadata.get().serialize(outputFile)
    }

    companion object {
        val FILE_NAME: String = fileName("github", Constants.File.Extension.JSON)
    }
}
