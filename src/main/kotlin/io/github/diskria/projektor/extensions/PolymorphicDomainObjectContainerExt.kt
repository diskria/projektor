package io.github.diskria.projektor.extensions

import org.gradle.api.PolymorphicDomainObjectContainer

internal inline fun <reified U : Any> PolymorphicDomainObjectContainer<in U>.maybeCreate(
    name: String,
    noinline configure: U.() -> Unit
): U = (findByName(name) as? U) ?: create(name, U::class.java, configure)
