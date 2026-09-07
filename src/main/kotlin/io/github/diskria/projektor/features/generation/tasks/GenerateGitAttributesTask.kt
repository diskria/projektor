package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.SIDE_EFFECTS
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.ProviderFactory
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = SIDE_EFFECTS)
abstract class GenerateGitAttributesTask @Inject internal constructor(
    providers: ProviderFactory,
    layout: ProjectLayout,
) : AbstractGenerateFileTask(providers, layout) {

    init {
        fileName.convention(".gitattributes")
        commitType.convention(CommitType.CHORE)
    }

    override fun build(): String =
        """
        * text=auto eol=lf
        *.sh text eol=lf
        *.bat text eol=crlf
        """.trimIndent()
}
