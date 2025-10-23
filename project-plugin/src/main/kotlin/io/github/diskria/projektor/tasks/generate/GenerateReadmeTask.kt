package io.github.diskria.projektor.tasks.generate

import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitMessage
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.joinBySpace
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektAbout
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.extensions.pushFiles
import io.github.diskria.projektor.readme.MarkdownHelper
import io.github.diskria.projektor.readme.shields.static.LicenseShield
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateReadmeTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:Internal
    abstract val repoDirectory: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        metadata.convention(project.getProjektMetadata())
        repoDirectory.convention(project.layout.projectDirectory)
        outputFile.convention(project.getFile(OUTPUT_FILE_NAME))
    }

    @TaskAction
    fun generate() {
        val metadata = metadata.get()
        val repoDirectory = repoDirectory.get().asFile
        val outputFile = outputFile.get().asFile

        val about = ProjektAbout.of(repoDirectory)
        val shields = buildList {
            addAll(metadata.publishingTargets.sorted().mapNotNull { it.mapToModel().getReadmeShield(metadata) })
            add(LicenseShield(metadata.license.mapToModel()))
        }
        val header = buildString {
            append(MarkdownHelper.header(metadata.name, 1))
            append(about.description)
            appendLine()
            appendLine()
            append(shields.map { it.buildMarkdown() }.joinBySpace())
        }
        val footer = buildString {
            val license = metadata.license.mapToModel()
            val licenseLink = MarkdownHelper.link(license.url, "${license.id} License")
            append(MarkdownHelper.header("License", 2))
            append("This project is licensed under the $licenseLink.")
        }
        val readmeText = buildString {
            append(header)
            append(MarkdownHelper.SEPARATOR)
            append(about.details)
            append(MarkdownHelper.SEPARATOR)
            append(footer)
            appendLine()
        }
        if (outputFile.exists() && outputFile.readText() == readmeText) {
            return
        }
        outputFile.writeText(readmeText)

        if (!EnvironmentHelper.isCI()) {
            return
        }
        metadata.repo.pushFiles(
            repoDirectory,
            CommitMessage(CommitType.DOCS, "update $OUTPUT_FILE_NAME"),
            outputFile
        )
    }

    companion object {
        private val OUTPUT_FILE_NAME: String = fileName("README", Constants.File.Extension.MARKDOWN)
    }
}
