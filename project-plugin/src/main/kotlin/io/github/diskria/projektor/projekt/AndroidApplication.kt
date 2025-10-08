package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.projekt.common.IProjekt

open class AndroidApplication(
    projekt: IProjekt,
    val config: AndroidApplicationConfiguration
) : IProjekt by projekt
