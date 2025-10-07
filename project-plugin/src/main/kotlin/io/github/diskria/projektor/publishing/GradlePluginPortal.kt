package io.github.diskria.projektor.publishing

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

data object GradlePluginPortal : PublishingTarget {

    override fun configure(projekt: IProjekt, project: Project) = with(project) {
        val plugin = projekt as? GradlePlugin ?: gradleError(
            "Only Gradle plugin projects supported for publishing to Modrinth" +
                    ", but got " + projekt::class.className()
        )
    }
}
