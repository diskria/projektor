package io.github.diskria.projektor.publishing.maven

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.getDirectory
import io.github.diskria.gradle.utils.extensions.hasTask
import io.github.diskria.gradle.utils.extensions.registerTask
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.GithubPagesShield
import io.github.diskria.projektor.tasks.release.ReleaseToGithubPagesTask
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import kotlin.collections.getValue

data object GithubPages : LocalMaven() {

    private const val MAVEN_DIRECTORY_NAME: String = "docs"

    override fun configure(projekt: IProjekt, project: Project) {
        super.configure(projekt, project)
        val rootProject = project.rootProject
        if (!rootProject.hasTask<ReleaseToGithubPagesTask>()) {
            rootProject.registerTask<ReleaseToGithubPagesTask> {
                dependsOn(getConfigurePublicationTaskName())

                val projektMetadata: ProjektMetadata by rootProject.extra.properties
                metadata.set(projektMetadata)
                localMavenDirectory.set(rootProject.getBuildDirectory(DIRECTORY_NAME))
                githubPagesMavenDirectory.set(rootProject.getDirectory(MAVEN_DIRECTORY_NAME))
            }
        }
    }

    override fun getReadmeShield(projekt: IProjekt): ReadmeShield =
        GithubPagesShield(projekt)
}
