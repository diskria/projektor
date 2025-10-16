package io.github.diskria.projektor.tasks.generate

import io.github.diskria.projektor.projekt.metadata.LicenseMetadata
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

abstract class GenerateLicenseTask : DefaultTask() {

    @get:Internal
    abstract val licenseMetadata: Property<LicenseMetadata>

    @get:OutputFile
    abstract val licenseFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val metadata = licenseMetadata.get()
        val licenseFile = licenseFile.get().asFile

        val licenseTag = SPDX_ID_PREFIX + metadata.license.id
        if (licenseFile.exists() && licenseFile.readLines().lastOrNull { it.isNotBlank() }?.trim() == licenseTag) {
            return
        }
        licenseFile.writeText(buildString {
            append(runBlocking { getLicenseText() })
            appendLine()
            append(licenseTag)
            appendLine()
        })
    }

    private suspend fun getLicenseText(): String =
        HttpClient(CIO).use { client ->
            val metadata = licenseMetadata.get()
            val template = client.get(metadata.license.templateUrl).bodyAsText()
            metadata.license.fillTemplate(template, metadata)
        }

    companion object {
        const val FILE_NAME: String = "LICENSE"

        private const val SPDX_ID_PREFIX: String = "SPDX ID: "
    }
}
