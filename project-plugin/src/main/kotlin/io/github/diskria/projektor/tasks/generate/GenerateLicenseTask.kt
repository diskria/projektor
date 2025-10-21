package io.github.diskria.projektor.tasks.generate

import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitMessage
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.extensions.getMetadata
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.extensions.pushFiles
import io.github.diskria.projektor.licenses.License
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateLicenseTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:Internal
    abstract val repoDirectory: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        metadata.convention(project.getMetadata())
        repoDirectory.convention(project.layout.projectDirectory)
        outputFile.convention(project.getFile(OUTPUT_FILE_NAME))
    }

    @TaskAction
    fun generate() {
        println("[GenerateLicenseTask] start")
        val metadata = metadata.get()
        val repoDirectory = repoDirectory.get().asFile
        val outputFile = outputFile.get().asFile

        val license = metadata.license.mapToModel()
        val licenseTag = SPDX_ID_PREFIX + license.id
        if (outputFile.exists() && outputFile.readLines().lastOrNull { it.isNotBlank() }?.trim() == licenseTag) {
            println("[GenerateLicenseTask] $OUTPUT_FILE_NAME already generated, skip")
            return
        }

        val licenseText = buildString {
            append(runBlocking { getLicenseText(metadata, license) })
            appendLine()
            append(licenseTag)
            appendLine()
        }
        if (outputFile.exists() && outputFile.readText() == licenseText) {
            println("[GenerateLicenseTask] $OUTPUT_FILE_NAME not changed, skip")
            return
        }
        outputFile.writeText(licenseText)

        if (!EnvironmentHelper.isCI()) {
            println("[GenerateLicenseTask] not running on CI, stop")
            return
        }
        metadata.repo.pushFiles(
            repoDirectory,
            CommitMessage(CommitType.DOCS, "update $OUTPUT_FILE_NAME"),
            outputFile
        )
        println("[GenerateLicenseTask] end")
    }

    private suspend fun getLicenseText(metadata: ProjektMetadata, license: License): String =
        HttpClient(CIO).use { client ->
            val template = client.get(license.templateUrl).bodyAsText()
            license.fillTemplate(template, metadata)
        }

    companion object {
        const val OUTPUT_FILE_NAME: String = "LICENSE"

        private const val SPDX_ID_PREFIX: String = "SPDX ID: "
    }
}
