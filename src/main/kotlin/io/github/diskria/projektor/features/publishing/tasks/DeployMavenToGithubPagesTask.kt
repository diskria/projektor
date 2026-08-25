package io.github.diskria.projektor.features.publishing.tasks

import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.extensions.isCI
import io.github.diskria.projektor.extensions.projektMetadata
import io.github.diskria.projektor.features.publishing.target.GithubPages
import io.github.diskria.projektor.internal.git.CommitMessage
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.SecretsHelper
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "Deploys Maven artifacts to GitHub Pages and performs Git pushes")
internal abstract class DeployMavenToGithubPagesTask @Inject constructor(private val secrets: SecretsHelper) : Sync() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val repoDir: DirectoryProperty

    init {
        applyProjektorGroup()

        metadata.convention(project.rootProject.projektMetadata)
        repoDir.convention(project.layout.projectDirectory)

        from(GithubPages.getLocalMavenDirectory(project))
        into(project.layout.projectDirectory.dir("docs"))

        val isCI = project.providers.isCI
        doLast {
            generateIndexTree(destinationDir)
            if (isCI) {
                metadata.get().repo.pushFile(
                    repoDir.get().asFile,
                    CommitMessage(CommitType.CHORE, "deploy maven to GitHub Pages"),
                    destinationDir,
                    secrets.githubToken,
                )
            }
        }
    }

    private fun generateIndexTree(directory: File, parentDirectory: File = directory) {
        val contents = directory.listFiles()?.sortedBy { it.name.lowercase() }?.ifEmpty { null } ?: return

        val isRootDirectory = directory == parentDirectory
        val directories = contents.filter { it.isDirectory }
        val files = contents.filter { it.isFile }

        val title = "Index of /${directory.relativeTo(parentDirectory).path}"
        val indexHtml = createHTML().html {
            lang = "en"
            head {
                meta(charset = Charsets.UTF_8.name())
                title { text(title) }
            }
            body {
                h2 { text(title) }
                hr {}
                ul {
                    if (!isRootDirectory) {
                        addLinkItem("../")
                    }
                    directories.forEach { directory ->
                        addLinkItem("${directory.name}/")
                    }
                    files.forEach { file ->
                        addLinkItem(file.name)
                    }
                }
            }
        }

        val indexFile = directory.resolve("index.html")
        indexFile.writeText(indexHtml)

        directories.forEach { generateIndexTree(it, parentDirectory) }
    }

    private fun UL.addLinkItem(href: String) =
        li {
            a(href = href) {
                text(href)
            }
        }
}
