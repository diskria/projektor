package io.github.diskria.projektor.extensions

import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal fun Task.applyProjektorGroup() {
    group = "projektor"
}

internal fun <T : Task> TaskProvider<T>.dependsSequentiallyOn(tasks: Iterable<TaskProvider<out Task>>) {
    val list = tasks.toList()
    if (list.isEmpty()) return
    list.windowed(2).forEach { (before, after) ->
        after.configure { it.mustRunAfter(before) }
    }
    configure { it.dependsOn(list) }
}
