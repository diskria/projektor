package io.github.diskria.projektor.publishing.common

import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import org.gradle.api.Project
import org.gradle.api.Task

abstract class PublishingTarget {

    abstract fun configure(projekt: Projekt, project: Project)

    abstract fun getPublishTaskName(): String

    abstract fun getHomepage(metadata: ProjektMetadata): String

    open fun configureDistributeTask(rootProject: Project): Task? = null

    open fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield? = null
}
