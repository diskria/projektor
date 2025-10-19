package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.registerExtension
import io.github.diskria.projektor.extensions.ensureTaskRegistered
import io.github.diskria.projektor.extensions.gradle.ProjektExtension
import io.github.diskria.projektor.tasks.generate.GenerateLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateReadmeTask
import io.github.diskria.projektor.tasks.generate.UpdateGithubRepositoryMetadataTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.registerExtension<ProjektExtension>()
        extension.onConfiguratorReady { configurator ->
            configurator.configure(project)
        }

        with(project.rootProject) {
            ensureTaskRegistered<GenerateLicenseTask>()
            ensureTaskRegistered<GenerateReadmeTask>()
            ensureTaskRegistered<UpdateGithubRepositoryMetadataTask>()
        }

        project.afterEvaluate {
            extension.ensureConfigured()
        }
    }

    companion object {
        val TASK_GROUP: String = "projektor"
    }
}
