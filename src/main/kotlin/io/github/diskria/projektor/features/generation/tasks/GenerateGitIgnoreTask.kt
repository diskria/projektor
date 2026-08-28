package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.Envs
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "Generates files and performs Git push side effects")
internal abstract class GenerateGitIgnoreTask @Inject internal constructor(envs: Envs) : AbstractGenerateFileTask(
    outputFileName = ".gitignore",
    commitType = CommitType.CHORE,
    envs = envs,
) {
    override fun getFileText(repoDirectory: File, file: File): String =
        """
        .idea/*
        !.idea/dictionaries/
        .gradle/
        .kotlin/
        build/
        """.trimIndent()
}
