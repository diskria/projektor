package io.github.diskria.projektor.features.distribution.tasks

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.features.distribution.target.GithubPagesDistributionTarget
import io.github.diskria.projektor.generated.Envs
import io.github.diskria.projektor.internal.git.CommitMessage
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.SIDE_EFFECTS
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Sync
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = SIDE_EFFECTS)
abstract class DeployMavenToGithubPagesTask @Inject constructor(
    private val envs: Envs,
    private val layout: ProjectLayout,
) : Sync() {

    @get:Input
    abstract val repo: Property<GithubRepo>

    init {
        applyProjektorGroup()
        from(GithubPagesDistributionTarget.getLocalMavenDirectory(layout))
        into(layout.projectDirectory.dir("docs"))
        doLast { deploy() }
    }

    private fun deploy() {
        generateIndexTree(destinationDir)
        if (!envs.isCI) return
        repo.get().pushFile(
            layout.projectDirectory.asFile,
            CommitMessage(CommitType.CHORE, "deploy maven to ${DistributionTargetType.GITHUB_PAGES.displayName}"),
            destinationDir,
            envs.githubToken,
        )
    }

    private fun generateIndexTree(directory: File) {
        val contents = directory.listFiles()?.sortedBy { it.name.lowercase() }?.ifEmpty { null } ?: return
        val directories = contents.filter { it.isDirectory }
        val title = "Index of /${directory.relativeTo(destinationDir).path}"
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
                    val hrefs = buildList {
                        if (directory != destinationDir) add("../")
                        directories.forEach { add("${it.name}/") }
                        contents.filter { it.isFile }.forEach { add(it.name) }
                    }
                    hrefs.forEach { href ->
                        li { a(href) { text(href) } }
                    }
                }
            }
        })
        directories.forEach { generateIndexTree(it) }
    }
}
