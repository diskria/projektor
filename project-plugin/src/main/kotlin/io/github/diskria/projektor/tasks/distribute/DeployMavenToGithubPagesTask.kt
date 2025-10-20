package io.github.diskria.projektor.tasks.distribute

import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.getDirectory
import io.github.diskria.kotlin.shell.dsl.GitShell
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.projektor.Environment
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.extensions.getMetadata
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.publishing.maven.common.LocalMavenBasedPublishingTarget
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Sync
import java.io.File

abstract class DeployMavenToGithubPagesTask : Sync() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:InputDirectory
    abstract val repositoryDirectory: DirectoryProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        metadata.convention(project.getMetadata())
        repositoryDirectory.convention(project.layout.projectDirectory)

        from(project.getBuildDirectory(LocalMavenBasedPublishingTarget.LOCAL_MAVEN_DIRECTORY_NAME))
        into(project.getDirectory(GITHUB_PAGES_MAVEN_DIRECTORY_NAME))

        doLast {
            generateIndexTree()
            if (Environment.isCI()) {
                deployToGithubPages()
            }
        }
    }

    private fun generateIndexTree(directory: File = destinationDir, parentDirectory: File = directory) {
        val contents = directory.listFiles()?.sortedBy { it.name.lowercase() }.toNullIfEmpty() ?: return

        val isRootDirectory = directory == parentDirectory
        val directories = contents.filter { it.isDirectory }
        val files = contents.filter { it.isFile }

        val title = "Index of " + Constants.Char.SLASH + directory.relativeTo(parentDirectory).path
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
                        addLinkItem(Constants.File.Path.PARENT_DIRECTORY)
                    }
                    directories.forEach { directory ->
                        addLinkItem(directory.name + Constants.Char.SLASH)
                    }
                    files.forEach { file ->
                        addLinkItem(file.name)
                    }
                }
            }
        }

        val indexFile = directory.resolve(fileName("index", "html"))
        indexFile.writeText(indexHtml)

        directories.forEach { generateIndexTree(it, parentDirectory) }
    }

    private fun deployToGithubPages() {
        with(GitShell.open(repositoryDirectory.get().asFile)) {
            val owner = metadata.get().repo.owner
            configureUser(owner.name, owner.email)
            stage(destinationDir.relativeTo(pwd()).path)
            commit("chore: deploy maven to GitHub Pages")
            push()
        }
    }

    private fun UL.addLinkItem(href: String) =
        li {
            a(href = href) {
                text(href)
            }
        }

    companion object {
        private const val GITHUB_PAGES_MAVEN_DIRECTORY_NAME: String = "docs"
    }
}
