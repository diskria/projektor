package io.github.diskria.projektor.tasks.generate

import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.kotlin.utils.extensions.generics.joinBySpace
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.readme.MarkdownHelper
import io.github.diskria.projektor.readme.shields.static.LicenseShield
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.extensions.core.extra

abstract class GenerateReadmeTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:OutputFile
    abstract val descriptionFile: RegularFileProperty

    @get:InputFile
    abstract val detailsFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        val projektMetadata: ProjektMetadata by project.extra.properties

        metadata.convention(projektMetadata)
        descriptionFile.convention(project.getFile(DESCRIPTION_FILE_NAME))
        detailsFile.convention(project.getFile(DETAILS_FILE_NAME))
        outputFile.convention(project.getFile(README_FILE_NAME))
    }

    @TaskAction
    fun generate() {
        val metadata = metadata.get()
        val shields = listOfNotNull(
            metadata.publishingTarget.mapToModel().getReadmeShield(metadata),
            LicenseShield(metadata.license.mapToModel())
        )
        val header = buildString {
            append(MarkdownHelper.header(metadata.name, 1))
            append(descriptionFile.get().asFile.readText().trim())
            appendLine()
            appendLine()
            append(shields.map { it.buildMarkdown() }.joinBySpace())
        }
        val details = detailsFile.get().asFile.readText().trim()
        val license = metadata.license.mapToModel()
        val footer = buildString {
            val licenseLink = MarkdownHelper.link(license.url, "${license.id} License")
            append(MarkdownHelper.header("License", 2))
            append("This project is licensed under the $licenseLink.")
        }
        val readme = buildString {
            append(header)
            append(MarkdownHelper.SEPARATOR)
            append(details)
            append(MarkdownHelper.SEPARATOR)
            append(footer)
            appendLine()
        }
        outputFile.get().asFile.writeText(readme)
    }

    companion object {
        private val DESCRIPTION_FILE_NAME: String = MarkdownHelper.fileName("DESCRIPTION")
        private val DETAILS_FILE_NAME: String = MarkdownHelper.fileName("DETAILS")
        private val README_FILE_NAME: String = MarkdownHelper.fileName("README")
    }
}
