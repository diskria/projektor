package io.github.diskria.projektor.publishing.external

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.AndroidApplication
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.ktor.http.*
import org.gradle.api.Project
import org.gradle.api.Task

data object GooglePlay : ExternalPublishingTarget() {

    override val publishTaskName: String get() = TODO()

    override fun configurePublishTask(projekt: Projekt, project: Project): Task? = with(project) {
        val application = projekt as? AndroidApplication ?: return null
        return project.tasks.named(publishTaskName).get()
    }

    override fun getHomepage(metadata: ProjektMetadata): String =
        buildUrl("play.google.com") {
            path("store", "apps", "details")
            parameters.append("id", metadata.packageNameBase)
        }

    override fun configureDistributeTask(rootProject: Project) = TODO()
}
