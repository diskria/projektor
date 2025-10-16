package io.github.diskria.projektor.publishing.maven

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.getDirectory
import io.github.diskria.gradle.utils.extensions.hasTask
import io.github.diskria.gradle.utils.extensions.registerTask
import io.github.diskria.projektor.common.projekt.ProjektMetadata
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.GithubPagesShield
import io.github.diskria.projektor.tasks.release.ReleaseToGithubPagesTask
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

data object GithubPages : LocalMaven() {

    override fun configure(projekt: IProjekt, project: Project) {
        super.configure(projekt, project)
        val rootProject = project.rootProject
        if (!rootProject.hasTask<ReleaseToGithubPagesTask>()) {
            rootProject.registerTask<ReleaseToGithubPagesTask> {
                val projektMetadata: ProjektMetadata by rootProject.extra.properties
                metadata.set(projektMetadata)
                repoDirectory.set(rootProject.rootDir)
                localMavenDirectory.set(rootProject.getBuildDirectory(LOCAL_MAVEN_DIRECTORY_NAME))
                githubPagesMavenDirectory.set(
                    rootProject.getDirectory(ReleaseToGithubPagesTask.GITHUB_PAGES_MAVEN_DIRECTORY_NAME)
                )
            }
        }
    }

    override fun getReadmeShield(projekt: IProjekt): ReadmeShield =
        GithubPagesShield(projekt)
}
