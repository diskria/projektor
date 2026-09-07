package io.github.diskria.projektor.features.release

import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.extensions.defaultTaskName
import io.github.diskria.projektor.features.generation.readme.tasks.GenerateReadmeTask
import io.github.diskria.projektor.features.generation.tasks.GenerateGitAttributesTask
import io.github.diskria.projektor.features.generation.tasks.GenerateGitIgnoreTask
import io.github.diskria.projektor.features.generation.tasks.GenerateLicenseTask
import io.github.diskria.projektor.features.generation.tasks.GenerateReleaseWorkflowTask
import io.github.diskria.projektor.features.metadata.tasks.UpdateGithubRepoMetadataTask
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.LIFECYCLE
import org.gradle.api.DefaultTask
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = LIFECYCLE)
abstract class ReleaseProjektTask : DefaultTask() {

    init {
        applyProjektorGroup()
    }

    companion object {
        val PREPARATION_TASK_NAMES: List<String> = listOf(
            defaultTaskName<GenerateGitAttributesTask>(),
            defaultTaskName<GenerateGitIgnoreTask>(),
            defaultTaskName<GenerateLicenseTask>(),
            defaultTaskName<GenerateReadmeTask>(),
            defaultTaskName<GenerateReleaseWorkflowTask>(),
            defaultTaskName<UpdateGithubRepoMetadataTask>(),
        )
        val PREPARATION_TASK_PATHS: List<String> = PREPARATION_TASK_NAMES.map { ":$it" }
    }
}
