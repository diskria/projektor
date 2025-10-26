package io.github.diskria.projektor.extensions

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.hasTask
import io.github.diskria.gradle.utils.extensions.jar
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

fun Project.getJarTask(): TaskProvider<out Jar> =
    if (hasTask(ShadowJar.SHADOW_JAR_TASK_NAME)) tasks.shadowJar
    else tasks.jar

fun Project.configureJarTask(configuration: Action<in Jar>) {
    getJarTask().configure(configuration)
}
