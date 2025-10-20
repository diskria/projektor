package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.Projekt

class GradlePlugin(projekt: Projekt, val config: GradlePluginConfiguration) : AbstractProjekt(projekt) {

    val id: String
        get() = packageName

    override val packageNameSuffix: String? =
        if (config.isSettingsPlugin) "settings"
        else null

    override fun getBuildConfigFields(): List<Property<String>> {
        val pluginName by name.autoNamedProperty(SCREAMING_SNAKE_CASE)
        val pluginVersion by version.autoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(pluginName, pluginVersion)
    }
}
