package io.github.diskria.projektor.tasks.generate

import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.emptyFileName
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.helpers.LineEndingModeType
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.tasks.generate.common.AbstractGenerateFileTask
import java.io.File

abstract class GenerateProjektGitAttributesTask : AbstractGenerateFileTask() {

    override fun getFileText(metadata: ProjektMetadata, repoDirectory: File, file: File): String =
        buildString {
            appendLine(
                buildLineEndingRule(
                    "${buildFileMask(null)} text=auto",
                    LineEndingModeType.LF
                )
            )
            appendLine(
                buildLineEndingRule(
                    "${buildFileMask(Constants.File.Extension.SHELL_SCRIPT)} text",
                    LineEndingModeType.LF
                )
            )
            appendLine(
                buildLineEndingRule(
                    "${buildFileMask(Constants.File.Extension.BAT_SCRIPT)} text",
                    LineEndingModeType.CRLF
                )
            )
        }

    override fun getOutputFileName(): String = emptyFileName("gitattributes")

    override fun getCommitType(): CommitType = CommitType.CHORE

    private fun buildFileMask(extension: String?): String =
        buildString {
            append(Constants.Char.ASTERISK)
            if (extension != null) {
                append(Constants.Char.DOT)
                append(extension)
            }
        }

    private fun buildLineEndingRule(attribute: String, type: LineEndingModeType): String =
        "$attribute eol=${type.getName()}"
}
