package io.github.diskria.projektor.tasks.generate.common

import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.ensureSuffix
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.pushFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class AbstractGenerateFileTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:Internal
    abstract val repoDirectory: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        metadata.convention(project.getProjektMetadata())
        repoDirectory.convention(project.layout.projectDirectory)
        outputFile.convention(project.getFile(getOutputFileName()))
    }

    @Internal
    abstract fun getFileText(metadata: ProjektMetadata, repoDirectory: File, file: File): String?

    @Internal
    abstract fun getOutputFileName(): String

    @Internal
    abstract fun getCommitType(): CommitType

    @TaskAction
    fun generate() {
        val metadata = metadata.get()
        val repoDirectory = repoDirectory.get().asFile
        val outputFile = outputFile.get().asFile

        val wasFileExists = outputFile.exists()
        val fileText = getFileText(metadata, repoDirectory, outputFile.ensureFileExists()) ?: return

        val oldText = outputFile.readText()
        val newText = fileText.trim().ensureSuffix(Constants.Char.NEW_LINE)
        if (newText == oldText) {
            return
        }
        outputFile.writeText(newText)
        metadata.repo.pushFile(repoDirectory, getCommitType(), outputFile, wasFileExists)
    }
}