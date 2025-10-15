package io.github.diskria.projektor.tasks.release

import io.github.diskria.kotlin.shell.dsl.GitShell
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.publishing.maven.GithubPages
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.Sync

abstract class ReleaseToGithubPagesTask : Sync() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:InputDirectory
    abstract val repoDirectory: DirectoryProperty

    @get:InputDirectory
    abstract val localMavenDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val githubPagesMavenDirectory: DirectoryProperty

    init {
        dependsOn(GithubPages.getConfigurePublicationTaskName())
        from(localMavenDirectory)
        into(githubPagesMavenDirectory)

        doLast {
            val metadata = metadata.get()
            val repoDirectory = repoDirectory.get().asFile
            val githubPagesMavenDirectory = githubPagesMavenDirectory.get().asFile

            with(GitShell.open(repoDirectory)) {
                configureUser(metadata.owner, metadata.email)
                stage(githubPagesMavenDirectory.relativeTo(repoDirectory).path)
                commit("chore: deploy ${metadata.version} release to GitHub Pages")
                push()
            }
        }
    }

    companion object {
        const val GITHUB_PAGES_MAVEN_DIRECTORY_NAME: String = "docs"
    }
}
