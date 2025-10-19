package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.projekt.common.IProjekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

open class KotlinLibrary(projekt: IProjekt, val config: KotlinLibraryConfiguration) : IProjekt by projekt {

    override val jvmTarget: JvmTarget
        get() = config.jvmTarget ?: super.jvmTarget

    override fun getBuildConfigFields(): List<Property<String>> {
        val libraryName by metadata.name.autoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(libraryName)
    }
}
