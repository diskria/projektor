package io.github.diskria.projektor.extensions

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublicationContainer
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

inline fun <reified T : Publication> PublicationContainer.registerIfAbsent(
    name: String,
    noinline configure: (T) -> Unit = {}
): NamedDomainObjectProvider<T> =
    if (names.contains(name)) named<T>(name)
    else register<T>(name, configure)
