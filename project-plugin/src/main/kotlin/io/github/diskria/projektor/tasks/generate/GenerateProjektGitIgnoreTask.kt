package io.github.diskria.projektor.tasks.generate

import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.generics.joinByNewLine
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.ProjektType.MINECRAFT_MOD
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.generate.common.AbstractGenerateFileTask
import java.io.File

abstract class GenerateProjektGitIgnoreTask : AbstractGenerateFileTask() {

    override fun getFileText(metadata: ProjektMetadata, repoDirectory: File, file: File): String {
        val patterns = mutableListOf(
            ".idea/*",
            "!.idea/dictionaries/",
            ".gradle/",
            ".kotlin/",
            "build/",
        )
        patterns.addAll(
            when (metadata.type) {
                MINECRAFT_MOD -> listOf(MinecraftMod.SHORT_NAME + Constants.Char.SLASH)
                else -> emptyList()
            }
        )
        return patterns.joinByNewLine()
    }

    override fun getOutputFileName(): String = ".gitignore"

    override fun getCommitType(): CommitType = CommitType.CHORE
}
