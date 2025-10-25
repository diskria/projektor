package io.github.diskria.projektor.tasks.generate

import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitMessage
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.generics.joinByNewLine
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.ProjektType.MINECRAFT_MOD
import io.github.diskria.projektor.extensions.pushFiles
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateProjektGitIgnoreTask : DefaultTask() {

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
        outputFile.convention(project.getFile(OUTPUT_FILE_NAME))
    }

    @TaskAction
    fun generate() {
        val metadata = metadata.get()
        val repoDirectory = repoDirectory.get().asFile
        val outputFile = outputFile.get().asFile.ensureFileExists()

        val ignorePatterns = mutableListOf(
            ".idea/*",
            "!.idea/dictionaries/",
            ".gradle/",
            ".kotlin/",
            "build/",
        )
        ignorePatterns.addAll(
            when (metadata.type) {
                MINECRAFT_MOD -> listOf("run")
                else -> emptyList()
            }
        )

        val gitIgnoreText = ignorePatterns.joinByNewLine()
        if (outputFile.readText() == gitIgnoreText) {
            return
        }
        outputFile.writeText(gitIgnoreText)

        if (!EnvironmentHelper.isCI()) {
            return
        }
        metadata.repo.pushFiles(
            repoDirectory,
            CommitMessage(CommitType.CHORE, "update $OUTPUT_FILE_NAME"),
            outputFile
        )
    }

    companion object {
        private const val OUTPUT_FILE_NAME: String = ".gitignore"
    }
}
