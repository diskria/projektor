package io.github.diskria.projektor.publishing.external

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import org.gradle.api.Project

data object GradlePluginPortal : ExternalPublishingTarget() {

    override fun configure(projekt: IProjekt, project: Project) = with(project) {
        val plugin = projekt as? GradlePlugin ?: gradleError(
            "Only Gradle plugin projects supported for publishing to Modrinth" +
                    ", but got " + projekt.metadata.type.getName(`kebab-case`)
        )
        TODO()
    }

    override fun getConfigurePublicationTaskName(): String = TODO()
}
