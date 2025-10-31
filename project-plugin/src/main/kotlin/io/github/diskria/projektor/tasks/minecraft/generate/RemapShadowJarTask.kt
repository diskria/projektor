package io.github.diskria.projektor.tasks.minecraft.generate

import io.github.diskria.projektor.ProjektorGradlePlugin
import net.fabricmc.loom.task.RemapJarTask

abstract class RemapShadowJarTask : RemapJarTask() {

    init {
        group = ProjektorGradlePlugin.TASK_GROUP
    }
}
