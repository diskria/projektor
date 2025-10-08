package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.settings.projekt.common.IProjekt

open class GradlePlugin(
    projekt: IProjekt,
    val config: GradlePluginConfiguration
) : IProjekt by projekt
