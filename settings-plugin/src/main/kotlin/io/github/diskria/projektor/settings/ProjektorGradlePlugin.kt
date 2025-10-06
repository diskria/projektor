package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.registerExtension
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class ProjektorGradlePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.registerExtension<ProjektExtension>()
    }
}
