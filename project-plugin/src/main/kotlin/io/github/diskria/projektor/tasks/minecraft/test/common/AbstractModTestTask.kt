package io.github.diskria.projektor.tasks.minecraft.test.common

import io.github.diskria.gradle.utils.extensions.getTask
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

abstract class AbstractModTestTask : DefaultTask() {

    @get:Internal
    abstract val minecraftMod: Property<MinecraftMod>

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        val tasksOrder = mutableListOf(
            project.getTask<GenerateModMixinsConfigTask>(),
            project.getTask<GenerateModConfigTask>(),
//            project.tasks.named("runDatagen").get(),
            project.tasks.named("sourcesJar").get(),
            project.tasks.named("remapJar").get(),
            project.tasks.named("build").get(),
        )
        tasksOrder.add(
            project.tasks.named(
                if (isServerSide()) "runServer"
                else "runClient"
            ).get()
        )

        dependsOn(tasksOrder)
        tasksOrder.windowed(2).forEach { (before, after) ->
            after.mustRunAfter(before)
        }
    }

    @Internal
    abstract fun isServerSide(): Boolean
}
