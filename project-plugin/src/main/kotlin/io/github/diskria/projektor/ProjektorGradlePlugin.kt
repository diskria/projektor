package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.ensureTaskRegistered
import io.github.diskria.gradle.utils.extensions.registerExtension
import io.github.diskria.projektor.extensions.gradle.ProjektExtension
import io.github.diskria.projektor.tasks.generate.GenerateLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateReadmeTask
import io.github.diskria.projektor.tasks.generate.UpdateGithubRepoMetadataTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        with(rootProject) {
            ensureTaskRegistered<GenerateLicenseTask>()
            ensureTaskRegistered<GenerateReadmeTask>()
            ensureTaskRegistered<UpdateGithubRepoMetadataTask>()
        }

        val extension = registerExtension<ProjektExtension>()
        extension.onConfiguratorReady { it.configure(this) }

        afterEvaluate {
            extension.ensureConfigured()
        }
    }

    companion object {
        val TASK_GROUP: String = ProjektBuildConfig.PLUGIN_NAME.lowercase()
    }
}
