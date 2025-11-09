package io.github.diskria.projektor.publishing.external

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.ktor.parameters
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.AndroidApplication
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.ktor.http.*
import org.gradle.api.Project

object GooglePlay : ExternalPublishingTarget() {

    override val publishTaskName: String = TODO()

    override fun configurePublishTask(projekt: Projekt, project: Project): Boolean = with(project) {
        return projekt is AndroidApplication
    }

    override fun getHomepage(metadata: ProjektMetadata): Url =
        buildUrl("play.google.com") {
            path("store", "apps", "details")
            parameters("id" to metadata.packageNameBase)
        }

    override fun configureDistributeTask(rootProject: Project) = TODO()
}
