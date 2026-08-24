package io.github.diskria.projektor.api

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class GradlePluginDsl @Inject internal constructor(objects: ObjectFactory) : ProjektorScope {
    val jvmTarget = objects.property<Int>()
    val javaVersion = objects.property<Int>()
    val supportsConfigurationCache = objects.property<Boolean>()
}
