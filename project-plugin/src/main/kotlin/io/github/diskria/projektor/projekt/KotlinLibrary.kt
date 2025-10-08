package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.projekt.common.IProjekt

open class KotlinLibrary(
    projekt: IProjekt,
    val config: KotlinLibraryConfiguration
) : IProjekt by projekt {

    override fun getMetadata(): List<Property<String>> {
        val libraryName by name.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(libraryName)
    }
}
