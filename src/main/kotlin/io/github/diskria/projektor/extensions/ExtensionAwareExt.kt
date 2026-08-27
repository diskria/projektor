package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.extra
import kotlin.reflect.KProperty0

internal inline fun <reified T> ExtensionAware.getExtra(property: KProperty0<*>): T? =
    if (extra.has(property.name)) extra.get(property.name) as? T else null

internal fun <T> ExtensionAware.setExtra(property: KProperty0<*>, value: T) {
    extra.set(property.name, value)
}

internal var Project.projektMetadata: ProjektMetadata?
    get() = ensureRootProject().getExtra<ProjektMetadata>(::projektMetadata)
    set(value) {
        ensureRootProject().setExtra(::projektMetadata, value)
    }
