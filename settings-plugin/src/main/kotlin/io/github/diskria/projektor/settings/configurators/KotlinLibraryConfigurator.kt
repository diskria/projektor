package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.settings.configurators.common.Configurator

open class KotlinLibraryConfigurator(
    config: KotlinLibraryConfiguration = KotlinLibraryConfiguration()
) : Configurator(config)
