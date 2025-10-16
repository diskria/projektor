package io.github.diskria.projektor.tasks.generate

import io.github.diskria.gradle.utils.extensions.getBuildFile
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.serialization.serialize
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.metadata.RepositoryMetadata
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.extensions.core.extra

abstract class GenerateRepositoryMetadataTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        val projektMetadata: ProjektMetadata by project.extra.properties

        metadata.convention(projektMetadata)
        outputFile.convention(project.getBuildFile("metadata/$OUTPUT_FILE_NAME"))
    }

    @TaskAction
    fun generate() {
        val metadata = metadata.get()
        val repositoryMetadata = RepositoryMetadata(
            homepage = null,
            description = metadata.description,
            topics = buildSet {
                add(metadata.type.getName(`kebab-case`))
                addAll(metadata.tags)
                add(metadata.publishingTarget.getName(`kebab-case`))
            },
        )
        val outputFile = outputFile.get().asFile
        outputFile.parentFile.mkdirs()
        repositoryMetadata.serialize(outputFile)
    }

    companion object {
        private val OUTPUT_FILE_NAME: String = fileName("repository", Constants.File.Extension.JSON)
    }
}
