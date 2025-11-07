package io.github.diskria.projektor.settings.extensions

import com.gradle.develocity.agent.gradle.DevelocityConfiguration
import io.github.diskria.gradle.utils.extensions.withPluginExtension
import org.gradle.api.initialization.Settings

fun Settings.develocity(configuration: DevelocityConfiguration.() -> Unit) {
    withPluginExtension<DevelocityConfiguration>("com.gradle.develocity", configuration)
}
