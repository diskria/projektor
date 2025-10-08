package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.settings.projekt.common.IProjekt

open class MinecraftMod(
    projekt: IProjekt,
    val config: MinecraftModConfiguration
) : IProjekt by projekt
