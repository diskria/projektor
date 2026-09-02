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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = SIDE_EFFECTS)
abstract class AbstractGenerateFileTask @Inject constructor(
    outputFileName: String,
    private val commitType: CommitType,
    private val env: EnvProvider,
    private val layout: ProjectLayout,
) : DefaultTask() {

    @get:Input
    abstract val repo: Property<GithubRepo>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        applyProjektorGroup()
        outputFile.set(layout.projectDirectory.file(outputFileName))
    }

    @TaskAction
    fun generate() {
        val repoDirectory = layout.projectDirectory.asFile
        val outputFile = outputFile.get().asFile
        val wasFileExists = outputFile.exists()
        if (!wasFileExists) outputFile.createNewFile()
        val fileText = getFileText(repoDirectory, outputFile)
        val oldText = outputFile.readText()
        val newText = fileText.trim() + "\n"
        if (newText == oldText) return
        outputFile.writeText(newText)
        if (!env.isCI) return
        repo.get().pushFile(repoDirectory, commitType, outputFile, wasFileExists, env.githubToken)
    }

    abstract fun getFileText(repoDirectory: File, file: File): String
}
