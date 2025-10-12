package io.github.diskria.projektor.tasks

import io.github.diskria.gradle.utils.extensions.tasks.GradleTask
import io.github.diskria.kotlin.utils.extensions.serialization.serialize
import io.github.diskria.projektor.projekt.metadata.GithubMetadata
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile

abstract class GenerateGithubMetadataTask : GradleTask() {

    @get:Internal
    abstract val metadata: Property<GithubMetadata>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    override fun runTask() {
        val outputFile = outputFile.get().asFile
        outputFile.parentFile.mkdirs()
        metadata.get().serialize(outputFile)
    }
}
