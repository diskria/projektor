package io.github.diskria.projektor.tasks.generate

import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.joinBySpace
import io.github.diskria.projektor.common.metadata.ProjektAbout
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.readme.MarkdownHelper
import io.github.diskria.projektor.readme.shields.static.LicenseShield
import io.github.diskria.projektor.tasks.generate.common.AbstractGenerateFileTask
import java.io.File

abstract class GenerateProjektReadmeTask : AbstractGenerateFileTask() {

    override fun getFileText(metadata: ProjektMetadata, repoDirectory: File, file: File): String =
        generateText(repoDirectory, metadata)

    override fun getOutputFileName(): String = fileName("README", Constants.File.Extension.MARKDOWN)

    override fun getCommitType(): CommitType = CommitType.DOCS

    companion object {
        fun generateText(repoDirectory: File, metadata: ProjektMetadata, isModrinthBody: Boolean = false): String {
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
            return buildString {
                append(header)
                append(MarkdownHelper.SEPARATOR)
                append(about.details)
                if (!isModrinthBody) {
                    append(MarkdownHelper.SEPARATOR)
                    append(footer)
                }
            }
        }
    }
}
