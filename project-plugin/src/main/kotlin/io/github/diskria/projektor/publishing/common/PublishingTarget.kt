package io.github.diskria.projektor.publishing.common

import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import org.gradle.api.Project

interface PublishingTarget {
    fun getTypeName(): String = this::class.className()
    fun configure(projekt: IProjekt, project: Project)
    fun getConfigurePublicationTaskName(): String
    fun getReadmeShield(projekt: IProjekt): ReadmeShield? = null
}
