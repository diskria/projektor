package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.core.model.github.GithubRepo
import io.github.diskria.projektor.extensions.applyProjektorGroup
import io.github.diskria.projektor.extensions.writeTextCreatingParent
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

@DisableCachingByDefault(because = SIDE_EFFECTS)
abstract class AbstractGenerateFileTask(
    private val providers: ProviderFactory,
    private val layout: ProjectLayout,
) : DefaultTask() {

    @get:Input
    abstract val fileName: Property<String>

    @get:Input
    abstract val commitType: Property<CommitType>

    @get:Input
    abstract val repo: Property<GithubRepo>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        applyProjektorGroup()
        outputFile.convention(layout.projectDirectory.file(fileName))
    }

    abstract fun build(): String

    @TaskAction
    fun generate() {
        val repoDirectory = layout.projectDirectory
        val targetFile = outputFile.get()
        val newText = build().trimEnd() + "\n"
        val isUpdate = targetFile.asFile.exists()
        if (isUpdate && newText == targetFile.asFile.readText()) return
        targetFile.writeTextCreatingParent(newText)
        val env = EnvProvider(providers)
        if (!env.isCI) return
        repo.get().pushFile(repoDirectory, commitType.get(), targetFile, isUpdate, env.githubToken)
    }
}
