package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.decapitalized
import io.github.diskria.projektor.internal.utils.requireNotNull
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra
import kotlin.reflect.KProperty0

internal inline fun <reified T> ExtensionAware.getExtra(property: KProperty0<*>): T? =
    if (extra.has(property.name)) extra.get(property.name) as? T else null

internal fun <T> ExtensionAware.setExtra(property: KProperty0<*>, value: T) {
    extra.set(property.name, value)
}

internal var Project.projektMetadata: ProjektMetadata
    get() = Errors.internal.requireNotNull(findProjektMetadata()) {
        "Projekt metadata has not been set yet"
    }
    set(value) {
        ensureRootProject().setExtra(::projektMetadata, value)
    }

internal fun Project.findProjektMetadata(): ProjektMetadata? =
    ensureRootProject().getExtra<ProjektMetadata>(::projektMetadata)

internal var Project.projektDistributeTaskNames: List<String>
    get() = getExtra<List<String>>(::projektDistributeTaskNames).orEmpty()
    set(value) {
        setExtra(::projektDistributeTaskNames, value)
    }

@PublishedApi
internal inline fun <reified E : Any> defaultExtensionName(): String =
    Errors.internal.requireNotNull(E::class.simpleName) {
        "Cannot derive extension name: class '${E::class}' does not have a simple name"
    }.removeSuffix("Extension").decapitalized()

internal inline fun <reified E : Any> ExtensionAware.registerExtension(
    vararg constructionArguments: Any,
    name: String = defaultExtensionName<E>(),
): E = extensions.create<E>(name, *constructionArguments)

internal inline fun <reified E : Any> ExtensionAware.hasExtension(name: String = defaultExtensionName<E>()): Boolean =
    extensions.findByName(name) is E
