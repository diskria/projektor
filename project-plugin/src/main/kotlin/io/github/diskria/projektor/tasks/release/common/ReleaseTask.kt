package io.github.diskria.projektor.tasks.release.common

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

abstract class ReleaseTask : DefaultTask() {

    @get:Internal
    abstract val targetTaskName: Property<String>

    init {
        dependsOn(targetTaskName)
    }
}
