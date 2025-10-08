package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.registerExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.registerExtension<ProjektExtension>()
        extension.onConfiguratorReady { configurator ->
            configurator.configure(project, extension.buildProjekt(project.rootProject))
        }
        project.afterEvaluate {
            extension.onProjectEvaluated()
        }
    }
}
