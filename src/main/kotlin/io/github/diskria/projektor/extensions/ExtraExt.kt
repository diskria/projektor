package io.github.diskria.projektor.extensions

import org.gradle.api.plugins.ExtraPropertiesExtension
import kotlin.reflect.KProperty0

internal inline fun <reified T> ExtraPropertiesExtension.get(key: KProperty0<*>): T? =
    if (has(key.name)) get(key.name) as? T else null

internal fun <T> ExtraPropertiesExtension.set(property: KProperty0<*>, value: T) {
    set(property.name, value)
}
