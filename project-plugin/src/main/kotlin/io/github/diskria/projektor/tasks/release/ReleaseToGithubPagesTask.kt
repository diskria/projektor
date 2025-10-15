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
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(localMavenDirectory)
        into(githubPagesMavenDirectory)
    }

    @TaskAction
    fun release() {
        val githubToken = Secrets.githubToken.toNullIfEmpty() ?: return

        val metadata = metadata.get()
        val repoDirectory = repoDirectory.get().asFile
        val localMavenDirectory = localMavenDirectory.get().asFile
        val githubPagesMavenDirectory = githubPagesMavenDirectory.get().asFile
        if (!localMavenDirectory.exists() || localMavenDirectory.listFiles().isEmpty()) {
            gradleError("Local maven directory does not exist")
        }
        with(GitShell.open(repoDirectory)) {
            println("pwd = ${pwd()}")
            configureUser(metadata.owner, metadata.email)
            setRemoteUrl(
                GitShell.ORIGIN_REMOTE_NAME,
                "https://x-access-token:${githubToken}@github.com/${metadata.owner}/${metadata.repo}.git"
            )
            println("Git working dir: ${repoDirectory.absolutePath}")
            println("Git pwd()        : ${GitShell.open(repoDirectory).pwd()}")
            println("Path to add      : ${githubPagesMavenDirectory.relativeTo(repoDirectory).path}")
            stage(".")
            val process = Runtime.getRuntime().exec(
                arrayOf("git", "add", githubPagesMavenDirectory.relativeTo(repoDirectory).path),
                null,
                repoDirectory
            )
            val exitCode = process.waitFor()
            println("Manual git add exit code: $exitCode")
            println(process.inputStream.bufferedReader().readText())
            println(process.errorStream.bufferedReader().readText())

            commit("feat: release to GitHub Pages")
            push()
        }
        println("Files in docs:")
        githubPagesMavenDirectory.walkTopDown().forEach { println(it) }

    }
}
