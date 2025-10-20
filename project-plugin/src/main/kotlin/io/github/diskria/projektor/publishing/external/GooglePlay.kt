package io.github.diskria.projektor.publishing.external

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.`Sentence case`
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadataExtra
import io.github.diskria.projektor.projekt.AndroidApplication
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.ktor.http.*
import org.gradle.api.Project

data object GooglePlay : ExternalPublishingTarget() {

    override fun configurePublishing(projekt: Projekt, project: Project) = with(project) {
        val androidApplication = projekt.asAndroidApplication()
        TODO()
    }

    override fun getPublishTaskName(): String = TODO()

    override fun getHomepage(metadata: ProjektMetadataExtra): String =
        buildUrl("play.google.com") {
            path("store", "apps", "details")
            parameters.append("id", metadata.packageNameBase)
        }

    override fun configureDistributeTask(project: Project) = TODO()

    private fun Projekt.asAndroidApplication(): AndroidApplication =
        this as? AndroidApplication ?: gradleError(
            "Only Android application projects supported for publishing to Google Play" +
                    ", but got " + type.getName(`Sentence case`)
        )
}
