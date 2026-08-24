package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.internal.utils.decapitalized
import org.gradle.api.plugins.ExtensionAware

@PublishedApi
internal val Class<*>.extensionName: String get() = simpleName.removeSuffix("Extension").decapitalized()

internal inline fun <reified E : Any> ExtensionAware.registerExtension(vararg arguments: Any): E {
    val jClass = E::class.java
    return extensions.create(jClass.extensionName, jClass, *arguments)
}

internal inline fun <reified E : Any> ExtensionAware.configureExtension(noinline configure: E.() -> Unit) {
    extensions.configure(E::class.java, configure)
}
