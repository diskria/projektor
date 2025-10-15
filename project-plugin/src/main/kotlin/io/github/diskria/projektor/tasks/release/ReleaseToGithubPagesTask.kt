package io.github.diskria.projektor.tasks.release

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.shell.dsl.GitShell
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

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
        from(localMavenDirectory)
        into(githubPagesMavenDirectory)
    }

    @TaskAction
    fun release() {
        val githubToken = Secrets.githubToken.toNullIfEmpty() ?: return

        val metadata = metadata.get()
        val repoDirectory = repoDirectory.get().asFile

        with(GitShell.open(repoDirectory)) {
            configureUser(metadata.owner, metadata.email)
            setRemoteUrl(
                GitShell.ORIGIN_REMOTE_NAME,
                "https://x-access-token:${githubToken}@github.com/${metadata.owner}/${metadata.repo}.git"
            )
            runGit(repoDirectory, "add", "--all")
            commit("feat: release to GitHub Pages")
            runGit(repoDirectory, "push", "origin", "main")
//            push()
        }
    }

    private fun runGit(repo: File, vararg args: String) {
        val process = ProcessBuilder(listOf("git") + args)
            .directory(repo)
            .redirectErrorStream(true)
            .start()
        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { println("[git] $it") }
        }
        val code = process.waitFor()
        println("[git] exit=$code\n")
    }
}
