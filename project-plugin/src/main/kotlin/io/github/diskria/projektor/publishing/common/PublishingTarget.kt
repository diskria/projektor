package io.github.diskria.projektor.publishing.common

import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import org.gradle.api.Project

interface PublishingTarget {
    fun getPublishTaskName(): String
    fun configure(projekt: IProjekt, project: Project)
    fun publish(projekt: IProjekt, project: Project)
    fun getReadmeShield(projekt: IProjekt): ReadmeShield? = null

    fun getTypeName(): String = this::class.className()
}
