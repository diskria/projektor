package io.github.diskria.projektor.tasks.generate

import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.licenses.License
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.extensions.core.extra

abstract class GenerateLicenseTask : DefaultTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:OutputFile
    abstract val licenseFile: RegularFileProperty

    init {
        val projektMetadata: ProjektMetadata by project.extra.properties

        metadata.convention(projektMetadata)
        licenseFile.convention(project.getFile(OUTPUT_FILE_NAME))
    }

    @TaskAction
    fun generate() {
        val metadata = metadata.get()
        val license = metadata.license.mapToModel()
        val licenseTag = SPDX_ID_PREFIX + license.id
        val licenseFile = licenseFile.get().asFile

        if (licenseFile.exists() && licenseFile.readLines().lastOrNull { it.isNotBlank() }?.trim() == licenseTag) {
            return
        }
        licenseFile.writeText(buildString {
            append(runBlocking { getLicenseText(metadata, license) })
            appendLine()
            append(licenseTag)
            appendLine()
        })
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
