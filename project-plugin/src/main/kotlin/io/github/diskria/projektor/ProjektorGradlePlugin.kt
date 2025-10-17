package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.registerExtension
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.ensureTaskRegistered
import io.github.diskria.projektor.extensions.mappers.mapToProjekt
import io.github.diskria.projektor.tasks.generate.GenerateLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateReadmeTask
import io.github.diskria.projektor.tasks.generate.UpdateGithubMetadataTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.registerExtension<ProjektExtension>()
        extension.onConfigured { configurator ->
            val projektMetadata: ProjektMetadata by project.rootProject.extra.properties
            configurator.configure(project, projektMetadata.mapToProjekt())
        }

        with(project.rootProject) {
            ensureTaskRegistered<GenerateLicenseTask>()
            ensureTaskRegistered<GenerateReadmeTask>()
            ensureTaskRegistered<UpdateGithubMetadataTask>()
        }

        project.afterEvaluate {
            extension.ensureConfigured()
        }
    }
}
