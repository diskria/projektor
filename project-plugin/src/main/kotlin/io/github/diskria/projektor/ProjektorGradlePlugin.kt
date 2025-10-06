package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.registerExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.registerExtension<ProjektExtension>()
    }
}
