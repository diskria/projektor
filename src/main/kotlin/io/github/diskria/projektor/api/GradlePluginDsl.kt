package io.github.diskria.projektor.api

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import javax.inject.Inject

abstract class GradlePluginDsl @Inject internal constructor(objects: ObjectFactory) : ProjektorScope {
    val description = objects.property<String>()
    val tags = objects.setProperty<String>()
    val version = objects.property<String>()
    val jvmTarget = objects.property<Int>()
    val javaVersion = objects.property<Int>()
    val supportsConfigurationCache = objects.property<Boolean>().convention(false)
}
