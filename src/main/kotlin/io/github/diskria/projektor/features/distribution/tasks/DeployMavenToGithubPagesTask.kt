package io.github.diskria.projektor.features.distribution.tasks

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.extensions.writeTextCreatingParent
import io.github.diskria.projektor.features.distribution.target.GithubPagesDistributionTarget
import io.github.diskria.projektor.generated.EnvProvider
import io.github.diskria.projektor.internal.git.CommitMessage
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.SIDE_EFFECTS
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Sync
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.relativeTo

@DisableCachingByDefault(because = SIDE_EFFECTS)
abstract class DeployMavenToGithubPagesTask @Inject internal constructor(
    private val providers: ProviderFactory,
    private val layout: ProjectLayout,
) : Sync() {

    @get:Input
    abstract val repo: Property<GithubRepo>

    @get:Internal
    abstract val repoDirectory: DirectoryProperty

    init {
        applyProjektorGroup()
        from(GithubPagesDistributionTarget.getLocalMavenDirectory(layout))
        into(repoDirectory.convention(layout.projectDirectory.dir("docs")))
        doLast { deploy() }
    }

    private fun deploy() {
        generateIndexTree(repoDirectory.get())
        val env = EnvProvider(providers)
        if (!env.isCI) return
        repo.get().pushFileOrDirectory(
            layout.projectDirectory,
            CommitMessage(CommitType.CHORE, "deploy maven to ${DistributionTargetType.GITHUB_PAGES.displayName}"),
            repoDirectory.get(),
            env.githubToken,
        )
    }

    private fun generateIndexTree(directory: Directory) {
        val allFiles = directory.asFile.listFiles() ?: emptyArray()
        val (directoryNames, fileNames) = allFiles.partition { it.isDirectory }.let { (directories, files) ->
            directories.map { it.name }.sortedBy { it.lowercase() } to files.map { it.name }.sortedBy { it.lowercase() }
        }
        val rootDirectory = repoDirectory.get()
        val isRoot = directory.asFile == rootDirectory.asFile
        val links = buildList {
            if (!isRoot) add("../")
            addAll(directoryNames.map { "$it/" })
            addAll(fileNames.filterNot { it == INDEX_FILE })
        }
        val relativePath = if (isRoot) {
            "/"
        } else {
            directory.asFile.toPath().relativeTo(rootDirectory.asFile.toPath()).invariantSeparatorsPathString
        }
        val title = "Index of $relativePath"
        directory.file(INDEX_FILE).writeTextCreatingParent(createHTML().html {
            lang = "en"
            head {
                meta(charset = Charsets.UTF_8.name())
                meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                title { text(title) }
                style { unsafe { +CSS_STYLE } }
            }
            body {
                h2 { text(title) }
                hr {}
                ul {
                    links.forEach { href ->
                        li { a(href) { text(href) } }
                    }
                }
            }
        })
        directoryNames.forEach { generateIndexTree(directory.dir(it)) }
    }

    private companion object {
        const val INDEX_FILE = "index.html"

        val CSS_STYLE =
            """
            body {
              font-family: system-ui,
              sans-serif;
              margin: 2rem;
            }
            a {
              text-decoration: none;
            }
            a:hover {
              text-decoration: underline;
            }
            """.trimIndent().replace(Regex("\\s+"), " ")
    }
}
