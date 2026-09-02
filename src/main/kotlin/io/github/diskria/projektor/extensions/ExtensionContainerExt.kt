package io.github.diskria.projektor.extensions

import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.create

internal inline fun <reified E : Any> defaultExtensionName(): String =
    defaultNameBySuffix<E>("Extension")

internal inline fun <reified E : Any> ExtensionContainer.create(
    vararg constructorArgs: Any,
    name: String = defaultExtensionName<E>(),
): E = create<E>(name, *constructorArgs)

internal inline fun <reified E : Any> ExtensionContainer.has(name: String = defaultExtensionName<E>()): Boolean =
    findByName(name) is E

internal inline fun <reified E : Any> ExtensionContainer.get(name: String = defaultExtensionName<E>()): E =
    getByName(name) as E

internal inline fun <reified E : Any> ExtensionContainer.find(name: String = defaultExtensionName<E>()): E? =
    if (has<E>(name)) get<E>(name)
    else null
