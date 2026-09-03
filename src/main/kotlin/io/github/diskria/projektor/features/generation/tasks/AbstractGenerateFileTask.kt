package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.generated.EnvProvider
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.SIDE_EFFECTS
import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

@DisableCachingByDefault(because = SIDE_EFFECTS)
abstract class AbstractGenerateFileTask(
    private val providers: ProviderFactory,
    private val layout: ProjectLayout,
) : DefaultTask() {

    @get:Input
    abstract val fileName: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Input
    abstract val repo: Property<GithubRepo>

    @get:Input
    abstract val commitType: Property<CommitType>

    init {
        applyProjektorGroup()
        outputFile.set(layout.projectDirectory.file(fileName))
    }

    @TaskAction
    fun generate() {
        val repoDirectory = layout.projectDirectory.asFile
        val targetFile = outputFile.get().asFile
        val wasFileExists = targetFile.exists()
        val fileText = getFileText(repoDirectory, targetFile)
        val newText = fileText.trim() + "\n"
        if (wasFileExists && newText == targetFile.readText()) return
        targetFile.parentFile?.mkdirs()
        targetFile.writeText(newText)
        val env = EnvProvider(providers)
        if (!env.isCI) return
        repo.get().pushFile(repoDirectory, commitType.get(), targetFile, wasFileExists, env.githubToken)
    }

    abstract fun getFileText(repoDirectory: File, file: File): String
}
