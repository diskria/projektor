package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.generated.EnvProvider
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.SIDE_EFFECTS
import org.gradle.api.file.ProjectLayout
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = SIDE_EFFECTS)
abstract class GenerateGitIgnoreTask @Inject internal constructor(
    env: EnvProvider,
    layout: ProjectLayout,
) : AbstractGenerateFileTask(".gitignore", CommitType.CHORE, env, layout) {

    override fun getFileText(repoDirectory: File, file: File): String =
        """
        .idea/*
        !.idea/dictionaries/
        .gradle/
        .kotlin/
        build/
        """.trimIndent()
}
