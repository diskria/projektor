package io.github.diskria.projektor.extensions

import org.gradle.api.plugins.PluginAware

internal fun PluginAware.ensurePluginApplied(id: String) {
    if (pluginManager.hasPlugin(id)) return
    pluginManager.apply(id)
}
