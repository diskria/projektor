package io.github.diskria.projektor.tasks

import io.github.diskria.gradle.utils.extensions.tasks.GradleTask
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.generics.joinBySpace
import io.github.diskria.projektor.projekt.metadata.ReadmeMetadata
import io.github.diskria.projektor.readme.MarkdownHelper
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile

abstract class GenerateReadmeTask : GradleTask() {

    @get:Internal
    abstract val metadata: Property<ReadmeMetadata>

    @get:InputFile
    abstract val aboutFile: RegularFileProperty

    @get:OutputFile
    abstract val readmeFile: RegularFileProperty

    override fun runTask() {
        val metadata = metadata.get()
        val aboutFile = aboutFile.get().asFile
        val readmeFile = readmeFile.get().asFile

        val projectSection = buildString {
            append(MarkdownHelper.header(metadata.name, 1))
            append(metadata.description)
            appendLine()
            appendLine()
            append(metadata.shields.map { it.buildMarkdown() }.joinBySpace())
        }
        val about = aboutFile.readText().trim()
        val licenseSection = buildString {
            append(MarkdownHelper.header("License", 2))
            append("This project is licensed under the ")
            append(MarkdownHelper.link(metadata.license.url, "${metadata.license.id} License"))
            append(Constants.Char.DOT)
        }
        readmeFile.writeText(
            buildString {
                append(projectSection)
                append(MarkdownHelper.SEPARATOR)
                append(about)
                append(MarkdownHelper.SEPARATOR)
                append(licenseSection)
                appendLine()
            }
        )
    }
}
