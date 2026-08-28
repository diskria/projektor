package io.github.diskria.projektor.api

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class KotlinLibraryDsl @Inject internal constructor(objects: ObjectFactory) : ProjektorScope {
    val description = objects.property<String>()
    val version = objects.property<String>()
    val jvmTarget = objects.property<Int>()
    val javaVersion = objects.property<Int>()
}
