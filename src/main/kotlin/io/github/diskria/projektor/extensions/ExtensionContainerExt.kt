package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.decapitalized
import io.github.diskria.projektor.internal.utils.requireNotNull
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.create

@PublishedApi
internal inline fun <reified E : Any> defaultExtensionName(): String =
    Errors.internal.requireNotNull(E::class.simpleName) {
        "Cannot derive extension name: class '${E::class}' does not have a simple name"
    }.removeSuffix("Extension").decapitalized()

internal inline fun <reified E : Any> ExtensionContainer.registerExtension(
    vararg constructionArguments: Any,
    name: String = defaultExtensionName<E>(),
): E = create<E>(name, *constructionArguments)

internal inline fun <reified E : Any> ExtensionContainer.hasExtension(
    name: String = defaultExtensionName<E>()
): Boolean = findByName(name) is E

internal inline fun <reified E : Any> ExtensionContainer.findExtension(name: String = defaultExtensionName<E>()): E? =
    if (!hasExtension<E>(name)) null
    else getByName(name) as E
