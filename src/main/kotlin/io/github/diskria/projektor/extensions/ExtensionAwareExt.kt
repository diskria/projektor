package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.decapitalized
import io.github.diskria.projektor.internal.utils.requireNotNull
import org.gradle.api.plugins.ExtensionAware

@PublishedApi
internal inline fun <reified E : Any> defaultExtensionName(): String =
    Errors.internal.requireNotNull(E::class.simpleName) {
        "Cannot derive extension name: class '${E::class}' does not have a simple name"
    }.removeSuffix("Extension").decapitalized()

internal inline fun <reified E : Any> ExtensionAware.registerExtension(
    vararg constructionArguments: Any,
    name: String = defaultExtensionName<E>(),
): E = extensions.create(name, E::class.java, *constructionArguments)

internal inline fun <reified E : Any> ExtensionAware.configureExtension(noinline configure: E.() -> Unit) {
    extensions.configure(E::class.java, configure)
}
