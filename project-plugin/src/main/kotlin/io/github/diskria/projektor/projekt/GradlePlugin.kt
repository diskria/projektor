package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import org.gradle.kotlin.dsl.provideDelegate

open class GradlePlugin(private val projekt: IProjekt) : IProjekt by projekt {

    val id: String = projekt.packageName

    var isSettingsPlugin: Boolean = false
    var tags: Set<String> = emptySet()

    override val packageName: String
        get() = projekt.packageName.modifyIf(isSettingsPlugin) { it.appendPackageName("settings") }

    override fun getMetadata(): List<Property<String>> {
        val pluginId by id.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        val pluginName by name.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(pluginId, pluginName)
    }
}
