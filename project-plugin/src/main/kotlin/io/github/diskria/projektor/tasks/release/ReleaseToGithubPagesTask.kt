package io.github.diskria.projektor.tasks.release

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.getDirectory
import io.github.diskria.kotlin.shell.dsl.GitShell
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.publishing.maven.GithubPages
import io.github.diskria.projektor.publishing.maven.common.LocalMaven.Companion.LOCAL_MAVEN_DIRECTORY_NAME
import org.gradle.api.tasks.Sync
import org.gradle.internal.extensions.core.extra

abstract class ReleaseToGithubPagesTask : Sync() {

    init {
        val projektMetadata: ProjektMetadata by project.extra.properties

        dependsOn(GithubPages.getConfigurePublicationTaskName())

        from(project.getBuildDirectory(LOCAL_MAVEN_DIRECTORY_NAME))
        into(project.getDirectory(GITHUB_PAGES_MAVEN_DIRECTORY_NAME))

        val repositoryDirectory = project.rootDir
        val owner = projektMetadata.repository.owner

        doLast {
            with(GitShell.open(repositoryDirectory)) {
                configureUser(owner.name, owner.email)
                stage(destinationDir.relativeTo(repositoryDirectory).path)
                commit("chore: deploy ${projektMetadata.version} release to GitHub Pages")
                push()
            }
        }
    }

    companion object {
        const val GITHUB_PAGES_MAVEN_DIRECTORY_NAME: String = "docs"
    }
}
