package io.github.diskria.projektor.tasks

import org.gradle.api.tasks.Sync
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named

abstract class UnarchiveProjektArtifactTask : Sync() {

    init {
        val jarTask = project.tasks.named<Jar>("jar")
        dependsOn(jarTask)

        val artifactFile = jarTask.flatMap { it.archiveFile }.get().asFile

        from(project.zipTree(artifactFile))
        into(artifactFile.parentFile.resolve(artifactFile.nameWithoutExtension))
    }
}
