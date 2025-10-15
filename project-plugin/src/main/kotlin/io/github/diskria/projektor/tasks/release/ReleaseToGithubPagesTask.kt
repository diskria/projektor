package io.github.diskria.projektor.tasks.release

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.shell.dsl.GitShell
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class ReleaseToGithubPagesTask : Copy() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:InputDirectory
    abstract val localMavenDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val githubPagesMavenDirectory: DirectoryProperty

    init {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(localMavenDirectory)
        into(githubPagesMavenDirectory)
    }

    @TaskAction
    fun release() {
        val githubToken = Secrets.githubToken.toNullIfEmpty() ?: return

        val metadata = metadata.get()
        val localMavenDirectory = localMavenDirectory.get().asFile
        val githubPagesMavenDirectory = githubPagesMavenDirectory.get().asFile
        if (!localMavenDirectory.exists()) {
            gradleError("Local maven directory does not exist")
        }
        if (githubPagesMavenDirectory.exists()) {
            githubPagesMavenDirectory.deleteRecursively()
        }
        with(GitShell.open(githubPagesMavenDirectory.parentFile)) {
            configureUser(metadata.owner, metadata.email)
            setRemoteUrl(
                GitShell.ORIGIN_REMOTE_NAME,
                "https://x-access-token:${githubToken}@github.com/${metadata.owner}/${metadata.repo}.git"
            )
            stage(githubPagesMavenDirectory.name)
            commit("feat: release to GitHub Pages")
            push()
        }
    }
}
