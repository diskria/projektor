package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.registerExtension
import io.github.diskria.projektor.common.projekt.ProjektMetadata
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.registerExtension<ProjektExtension>()
        extension.onConfiguratorReady { configurator ->
            val projektMetadata: ProjektMetadata by project.rootProject.extra.properties
            val projekt = extension.buildProjekt(projektMetadata)
            configurator.configure(project, projekt)
        }
        project.afterEvaluate {
            extension.checkNotConfigured()
        }
    }
}
