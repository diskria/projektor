package io.github.diskria.projektor.extensions

import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.create

@PublishedApi
internal inline fun <reified E : Any> defaultExtensionName(): String =
    checkNotNull(E::class.simpleName) {
        "Cannot derive extension name: class '${E::class}' does not have a simple name"
    }.removeSuffix("Extension").decapitalized()

internal inline fun <reified E : Any> ExtensionContainer.create(
    vararg constructionArguments: Any,
    name: String = defaultExtensionName<E>(),
): E = create<E>(name, *constructionArguments)

internal inline fun <reified E : Any> ExtensionContainer.has(name: String = defaultExtensionName<E>()): Boolean =
    findByName(name) is E

internal inline fun <reified E : Any> ExtensionContainer.get(name: String = defaultExtensionName<E>()): E =
    getByName(name) as E

internal inline fun <reified E : Any> ExtensionContainer.find(name: String = defaultExtensionName<E>()): E? =
    if (!has<E>(name)) null
    else get<E>(name)
