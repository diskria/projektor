package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.Envs
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "Generates files and performs Git push side effects")
internal abstract class AbstractGenerateFileTask @Inject constructor(
    outputFileName: String,
    private val commitType: CommitType,
    private val envs: Envs,
) : DefaultTask() {

    @get:Input
    abstract val repo: Property<GithubRepo>

    @get:Internal
    abstract val repoDirectory: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        applyProjektorGroup()
        repoDirectory.convention(project.layout.projectDirectory)
        outputFile.convention(project.layout.projectDirectory.file(outputFileName))
    }

    @TaskAction
    fun generate() {
        val repoDirectory = repoDirectory.get().asFile
        val outputFile = outputFile.get().asFile
        val wasFileExists = outputFile.exists()
        if (!wasFileExists) outputFile.createNewFile()
        val fileText = getFileText(repoDirectory, outputFile) ?: return
        val oldText = outputFile.readText()
        val newText = fileText.trim() + "\n"
        if (newText == oldText) return
        outputFile.writeText(newText)
        if (envs.isCI) {
            repo.get().pushFile(repoDirectory, commitType, outputFile, wasFileExists, envs.githubToken)
        }
    }

    abstract fun getFileText(repoDirectory: File, file: File): String?
}
