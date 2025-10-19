package io.github.diskria.projektor.tasks.generate

import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.kotlin.shell.dsl.GitShell
import io.github.diskria.projektor.ProjektorGradlePlugin
import io.github.diskria.projektor.common.extensions.getMetadataExtra
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadataExtra
import io.github.diskria.projektor.extensions.mappers.mapToModel
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
    abstract val metadata: Property<ProjektMetadataExtra>

    @get:Internal
    abstract val repositoryDirectory: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = ProjektorGradlePlugin.TASK_GROUP

        metadata.convention(project.getMetadataExtra())
        repositoryDirectory.convention(project.layout.projectDirectory)
        outputFile.convention(project.getFile(OUTPUT_FILE_NAME))
    }

    @TaskAction
    fun generate() {
        val metadata = metadata.get()
        val outputFile = outputFile.get().asFile

        val license = metadata.license.mapToModel()
        val licenseTag = SPDX_ID_PREFIX + license.id
        if (outputFile.exists() && outputFile.readLines().lastOrNull { it.isNotBlank() }?.trim() == licenseTag) {
            return
        }

        val licenseText = buildString {
            append(runBlocking { getLicenseText(metadata, license) })
            appendLine()
            append(licenseTag)
            appendLine()
        }
        if (outputFile.exists() && outputFile.readText() == licenseText) {
            return
        }
        outputFile.writeText(licenseText)

        with(GitShell.open(repositoryDirectory.get().asFile)) {
            val owner = metadata.repository.owner
            configureUser(owner.name, owner.email)
            stage(outputFile.relativeTo(pwd()).path)
            commit("docs: update $OUTPUT_FILE_NAME")
            push()
        }
    }

    private suspend fun getLicenseText(metadata: ProjektMetadataExtra, license: License): String =
        HttpClient(CIO).use { client ->
            val template = client.get(license.templateUrl).bodyAsText()
            license.fillTemplate(template, metadata)
        }

    companion object {
        const val OUTPUT_FILE_NAME: String = "LICENSE"

        private const val SPDX_ID_PREFIX: String = "SPDX ID: "
    }
}
