package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.projekt.common.IProjekt

open class GradlePlugin(projekt: IProjekt, val config: GradlePluginConfiguration) : IProjekt by projekt {

    val id: String
        get() = packageName

    override val packageNameSuffix: String? =
        if (config.isSettingsPlugin) "settings"
        else null

    override fun getBuildConfigFields(): List<Property<String>> {
        val pluginId by id.autoNamedProperty(SCREAMING_SNAKE_CASE)
        val pluginName by metadata.name.autoNamedProperty(SCREAMING_SNAKE_CASE)
        val pluginVersion by metadata.version.autoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(pluginId, pluginName, pluginVersion)
    }
}
