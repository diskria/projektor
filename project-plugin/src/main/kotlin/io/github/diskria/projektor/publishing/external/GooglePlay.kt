package io.github.diskria.projektor.publishing.external

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.projektor.projekt.AndroidApplication
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import org.gradle.api.Project

data object GooglePlay : ExternalPublishingTarget() {

    override fun configure(projekt: IProjekt, project: Project) = with(project) {
        val application = projekt as? AndroidApplication ?: gradleError(
            "Only Android application projects supported for publishing to Modrinth" +
                    ", but got " + projekt::class.className()
        )
        TODO()
    }

    override fun getConfigurePublicationTaskName(): String = TODO()
}
