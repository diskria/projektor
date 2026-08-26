package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.extensions.isCI
import io.github.diskria.projektor.extensions.projektMetadata
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
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
    private val providers: ProviderFactory,
    private val secrets: SecretsHelper,
) : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:Internal
    abstract val repoDirectory: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        applyProjektorGroup()
        metadata.convention(project.projektMetadata)
        repoDirectory.convention(project.layout.projectDirectory)
        outputFile.convention(project.layout.projectDirectory.file(outputFileName))
    }

    @TaskAction
    fun generate() {
        val metadata = metadata.get()
        val repoDirectory = repoDirectory.get().asFile
        val outputFile = outputFile.get().asFile
        val wasFileExists = outputFile.exists()
        if (!wasFileExists) outputFile.createNewFile()
        val fileText = getFileText(metadata, repoDirectory, outputFile) ?: return
        val oldText = outputFile.readText()
        val newText = fileText.trim() + "\n"
        if (newText == oldText) return
        outputFile.writeText(newText)
        if (providers.isCI) {
            metadata.repo.pushFile(repoDirectory, commitType, outputFile, wasFileExists, secrets.githubToken)
        }
    }

    abstract fun getFileText(metadata: ProjektMetadata, repoDirectory: File, file: File): String?
}
