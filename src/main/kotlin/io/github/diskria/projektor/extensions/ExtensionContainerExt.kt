package io.github.diskria.projektor.extensions

import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.create

@PublishedApi
internal inline fun <reified E : Any> defaultExtensionName(): String =
    defaultNameBySuffix<E>("Extension")

inline fun <reified E : Any> ExtensionContainer.create(
    vararg constructorArgs: Any,
    name: String = defaultExtensionName<E>(),
): E = create<E>(name, *constructorArgs)
