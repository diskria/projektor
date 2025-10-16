package io.github.diskria.projektor.publishing.common

import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import org.gradle.api.Project

interface PublishingTarget {
    fun configure(projekt: IProjekt, project: Project)
    fun getConfigurePublicationTaskName(): String
    fun getReadmeShield(projekt: IProjekt): ReadmeShield? = null
}
