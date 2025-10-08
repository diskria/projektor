package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.settings.projekt.common.IProjekt

open class AndroidApplication(
    projekt: IProjekt,
    val config: AndroidApplicationConfiguration
) : IProjekt by projekt
