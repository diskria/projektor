package io.github.diskria.projektor.tasks.minecraft.test.common

import io.github.diskria.gradle.utils.extensions.getTask
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.extensions.children
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.named

abstract class AbstractTestModTask : DefaultTask() {

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        val side = getSide()
        val sideProject = project.children().first { it.name == side.getName() }

        val tasksOrder = mutableListOf(
            project.getTask<GenerateModMixinsConfigTask>(),
            project.getTask<GenerateModConfigTask>(),
            project.tasks.named("build").get(),
            sideProject.tasks.named<JavaExec>("run" + side.getName(PascalCase)).get()
        )

        dependsOn(tasksOrder)
        tasksOrder.windowed(2).forEach { (before, after) ->
            after.mustRunAfter(before)
        }
    }

    @Internal
    abstract fun getSide(): ModSide
}
