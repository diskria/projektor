package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.ensureTaskRegistered
import io.github.diskria.gradle.utils.extensions.registerExtension
import io.github.diskria.projektor.common.ProjectModules
import io.github.diskria.projektor.extensions.gradle.ProjektExtension
import io.github.diskria.projektor.tasks.UpdateProjektRepoMetadataTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektGitAttributesTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektGitIgnoreTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektReadmeTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        if (path != ProjectModules.Common.PATH) {
            with(rootProject) {
                ensureTaskRegistered<GenerateProjektGitAttributesTask>()
                ensureTaskRegistered<GenerateProjektGitIgnoreTask>()
                ensureTaskRegistered<GenerateProjektLicenseTask>()
                ensureTaskRegistered<GenerateProjektReadmeTask>()
                ensureTaskRegistered<UpdateProjektRepoMetadataTask>()
            }
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
