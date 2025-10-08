package io.github.diskria.projektor.projekt

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.projekt.common.IProjekt

open class GradlePlugin(
    projekt: IProjekt,
    val config: GradlePluginConfiguration
) : IProjekt by projekt {

    val id: String
        get() = packageName

    override val packageName: String
        get() = super.packageName.modifyIf(config.isSettingsPlugin) { it.appendPackageName("settings") }

    override fun getMetadata(): List<Property<String>> {
        val pluginId by id.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        val pluginName by name.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(pluginId, pluginName)
    }
}
