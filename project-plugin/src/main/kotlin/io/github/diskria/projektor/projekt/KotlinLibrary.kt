package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import org.gradle.kotlin.dsl.provideDelegate

open class KotlinLibrary(private val projekt: IProjekt) : IProjekt by projekt {

    override fun getMetadata(): List<Property<String>> {
        val libraryName by name.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(libraryName)
    }
}
