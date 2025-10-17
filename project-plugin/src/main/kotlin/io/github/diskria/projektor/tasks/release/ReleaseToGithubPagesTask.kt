package io.github.diskria.projektor.tasks.release

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.getDirectory
import io.github.diskria.kotlin.shell.dsl.GitShell
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.publishing.maven.GithubPages
import io.github.diskria.projektor.publishing.maven.common.LocalMaven.Companion.LOCAL_MAVEN_DIRECTORY_NAME
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Sync
import org.gradle.internal.extensions.core.extra

abstract class ReleaseToGithubPagesTask : Sync() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:Internal
    abstract val repositoryDirectory: DirectoryProperty

    init {
        val projektMetadata: ProjektMetadata by project.extra.properties

        dependsOn(GithubPages.getConfigurePublicationTaskName())

        metadata.convention(projektMetadata)
        repositoryDirectory.convention(project.layout.projectDirectory)

        from(project.getBuildDirectory(LOCAL_MAVEN_DIRECTORY_NAME))
        into(project.getDirectory(GITHUB_PAGES_MAVEN_DIRECTORY_NAME))

        doLast {
            val metadata = metadata.get()
            val repositoryDirectory = repositoryDirectory.get().asFile

            with(GitShell.open(repositoryDirectory)) {
                val owner = metadata.repository.owner
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
