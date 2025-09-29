package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.kotlin.registerExtension
import io.github.diskria.projektor.extensions.gradle.ProjektExtension
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class ProjektorSettingsGradlePlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.registerExtension<ProjektExtension>()
    }
}
