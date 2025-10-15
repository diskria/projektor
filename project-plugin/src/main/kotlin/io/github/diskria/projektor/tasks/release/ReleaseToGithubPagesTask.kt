package io.github.diskria.projektor.tasks.release

import io.github.diskria.kotlin.shell.dsl.GitShell
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class ReleaseToGithubPagesTask : Sync() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:InputDirectory
    abstract val repoDirectory: DirectoryProperty

    @TaskAction
    fun release() {
        super.copy()

//        val githubToken = Secrets.githubToken.toNullIfEmpty() ?: return

        val metadata = metadata.get()
        val repoDirectory = repoDirectory.get().asFile

        with(GitShell.open(repoDirectory)) {
            configureUser(metadata.owner, metadata.email)
//            setRemoteUrl(
//                GitShell.ORIGIN_REMOTE_NAME,
//                "https://x-access-token:${githubToken}@github.com/${metadata.owner}/${metadata.repo}.git"
//            )
            stage("--all")
            commit("chore: deploy ${metadata.version} release to GitHub Pages")
            push()
        }
    }
}
