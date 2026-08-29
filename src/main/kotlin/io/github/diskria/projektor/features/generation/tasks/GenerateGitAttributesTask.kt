package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.Envs
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault
abstract class GenerateGitAttributesTask @Inject constructor(
    envs: Envs,
) : AbstractGenerateFileTask(".gitattributes", CommitType.CHORE, envs) {

    override fun getFileText(repoDirectory: File, file: File): String =
        """
        * text=auto eol=lf
        *.sh text eol=lf
        *.bat text eol=crlf
        """.trimIndent()
}
