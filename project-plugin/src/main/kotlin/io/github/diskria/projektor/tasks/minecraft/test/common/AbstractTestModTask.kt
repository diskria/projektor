package io.github.diskria.projektor.tasks.minecraft.test.common

import io.github.diskria.gradle.utils.extensions.build
import io.github.diskria.gradle.utils.extensions.children
import io.github.diskria.gradle.utils.extensions.dependsSequentiallyOn
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.named

abstract class AbstractTestModTask : DefaultTask() {

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        val side = getSide()
        val sideProject = project.children.first { it.name == side.getName() }
        dependsSequentiallyOn(
            listOf(
                project.tasks.build.get(),
                sideProject.tasks.named<JavaExec>("run" + side.getName(PascalCase)).get()
            )
        )
    }

    @Internal
    abstract fun getSide(): ModSide
}
