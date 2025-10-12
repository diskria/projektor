package io.github.diskria.projektor.publishing

import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.markdown.shields.ReadmeShield
import org.gradle.api.Project

sealed interface PublishingTarget {
    fun configure(projekt: IProjekt, project: Project)
    fun getTypeName(): String = this::class.className()
    fun getReadmeShield(projekt: IProjekt): ReadmeShield? = null
}
