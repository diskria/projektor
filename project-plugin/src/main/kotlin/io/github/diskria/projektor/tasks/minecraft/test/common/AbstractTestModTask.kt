package io.github.diskria.projektor.tasks.minecraft.test.common

import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal

abstract class AbstractTestModTask : DefaultTask() {

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }

    @Internal
    abstract fun getSide(): ModSide
}
