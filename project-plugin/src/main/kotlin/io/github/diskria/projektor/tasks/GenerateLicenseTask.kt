package io.github.diskria.projektor.tasks

import io.github.diskria.gradle.utils.extensions.tasks.GradleTask
import io.github.diskria.projektor.common.projekt.ProjektMetadata
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile

abstract class GenerateLicenseTask : GradleTask() {

    @get:Internal
    abstract val metadata: Property<ProjektMetadata>

    @get:OutputFile
    abstract val licenseFile: RegularFileProperty

    override fun runTask() {
        val metadata = metadata.get()
        val licenseFile = licenseFile.get().asFile

        val licenseTag = "SPDX ID: ${metadata.license.id}"
        if (licenseFile.exists() && licenseFile.readLines().lastOrNull { it.isNotBlank() }?.trim() == licenseTag) {
            return
        }
        licenseFile.writeText(buildString {
            append(runBlocking { getLicenseText(metadata) })
            appendLine()
            append(licenseTag)
            appendLine()
        })
    }

    private suspend fun getLicenseText(metadata: ProjektMetadata): String =
        HttpClient(CIO).use { client ->
            val template = client.get(metadata.license.templateUrl).bodyAsText()
            metadata.license.fillTemplate(template, metadata)
        }

    companion object {
        const val FILE_NAME: String = "LICENSE"
    }
}
