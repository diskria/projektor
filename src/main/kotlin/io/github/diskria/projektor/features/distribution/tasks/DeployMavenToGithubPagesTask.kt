package io.github.diskria.projektor.features.distribution.tasks

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.internal.git.CommitMessage
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.Envs
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Sync
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "Deploys Maven artifacts to GitHub Pages and performs Git pushes")
abstract class DeployMavenToGithubPagesTask @Inject constructor(private val envs: Envs) : Sync() {

    @get:Input
    abstract val repo: Property<GithubRepo>

    @get:Internal
    abstract val repoDirectory: DirectoryProperty

    init {
        applyProjektorGroup()
        doLast { deploy() }
    }

    private fun deploy() {
        generateIndexTree(destinationDir)
        if (!envs.isCI) return
        repo.get().pushFile(
            repoDirectory.get().asFile,
            CommitMessage(CommitType.CHORE, "deploy maven to ${DistributionTargetType.GITHUB_PAGES.displayName}"),
            destinationDir,
            envs.githubToken,
        )
    }

    private fun generateIndexTree(directory: File, parentDirectory: File = directory) {
        val contents = directory.listFiles()?.sortedBy { it.name.lowercase() }?.ifEmpty { null } ?: return
        val directories = contents.filter { it.isDirectory }
        val title = "Index of /${directory.relativeTo(parentDirectory).path}"
        directory.resolve("index.html").writeText(createHTML().html {
            lang = "en"
            head {
                meta(charset = Charsets.UTF_8.name())
                title { text(title) }
            }
            body {
                h2 { text(title) }
                hr {}
                ul {
                    if (directory != parentDirectory) addLinkItem("../")
                    directories.forEach { addLinkItem("${it.name}/") }
                    contents.filter { it.isFile }.forEach { addLinkItem(it.name) }
                }
            }
        })
        directories.forEach { generateIndexTree(it, parentDirectory) }
    }
}

private fun UL.addLinkItem(href: String) = li { a(href) { text(href) } }
