package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.kotlin.registerExtension
import io.github.diskria.projektor.settings.extensions.gradle.ProjektExtension
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class ProjektorSettingsGradlePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.registerExtension<ProjektExtension>()
    }
}
