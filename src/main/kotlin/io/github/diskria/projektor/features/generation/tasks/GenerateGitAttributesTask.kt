package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.provider.ProviderFactory
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "Generates files and performs Git push side effects")
internal abstract class GenerateGitAttributesTask @Inject constructor(
    providers: ProviderFactory,
    secrets: SecretsHelper,
) : AbstractGenerateFileTask(
    outputFileName = ".gitattributes",
    commitType = CommitType.CHORE,
    providers = providers,
    secrets = secrets,
) {
    override fun getFileText(repoDirectory: File, file: File): String =
        """
        * text=auto eol=lf
        *.sh text eol=lf
        *.bat text eol=crlf
        """.trimIndent()
}
