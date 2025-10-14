package io.github.diskria.projektor.publishing.maven

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.getDirectory
import io.github.diskria.gradle.utils.extensions.registerTask
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.GithubPagesShield
import io.github.diskria.projektor.tasks.release.ReleaseToGithubPagesTask
import org.gradle.api.Project

data object GithubPages : LocalMaven() {

    private const val MAVEN_DIRECTORY_NAME: String = "github-pages-maven"

    override fun configure(projekt: IProjekt, project: Project) {
        super.configure(projekt, project)
        project.registerTask<ReleaseToGithubPagesTask> {
            localMavenDirectory.set(project.rootProject.getBuildDirectory(DIRECTORY_NAME))
            githubPagesMavenDirectory.set(project.rootProject.getDirectory(MAVEN_DIRECTORY_NAME))
        }
    }

    override fun getReadmeShield(projekt: IProjekt): ReadmeShield =
        GithubPagesShield(projekt)
}
