package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.Projekt

class GradlePlugin(projekt: Projekt, val config: GradlePluginConfiguration) : AbstractProjekt(projekt) {

    val id: String
        get() = packageName

    override val packageNameSuffix: String? =
        if (config.isSettingsPlugin) "settings"
        else null
}
