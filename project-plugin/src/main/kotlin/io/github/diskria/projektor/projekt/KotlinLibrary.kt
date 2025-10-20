package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.Projekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class KotlinLibrary(projekt: Projekt, val config: KotlinLibraryConfiguration) : AbstractProjekt(projekt) {

    override val jvmTarget: JvmTarget
        get() = config.jvmTarget ?: super.jvmTarget

    override fun getBuildConfigFields(): List<Property<String>> {
        val libraryName by name.autoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(libraryName)
    }
}
