package io.github.diskria.projektor.tasks

import io.github.diskria.projektor.extensions.getJarTask
import org.gradle.api.tasks.Sync

abstract class UnarchiveProjektArtifactTask : Sync() {

    init {
        val jarTask = project.getJarTask()
        dependsOn(jarTask)

        val artifactFile = jarTask.flatMap { it.archiveFile }.get().asFile

        from(project.zipTree(artifactFile))
        into(artifactFile.parentFile.resolve(artifactFile.nameWithoutExtension))
    }
}
