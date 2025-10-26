package io.github.diskria.projektor.tasks.generate

import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.tasks.generate.common.AbstractGenerateFileTask
import java.io.File

abstract class GenerateProjektGitAttributesTask : AbstractGenerateFileTask() {

    override fun getFileText(metadata: ProjektMetadata, repoDirectory: File, file: File): String =
        buildString {
            appendLine("* text=auto eol=lf")
            appendLine("*.sh text eol=lf")
            appendLine("*.bat text eol=crlf")
        }

    override fun getOutputFileName(): String = ".gitattributes"

    override fun getCommitType(): CommitType = CommitType.CHORE
}
