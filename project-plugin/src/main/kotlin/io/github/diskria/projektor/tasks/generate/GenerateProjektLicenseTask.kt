package io.github.diskria.projektor.tasks.generate

import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.tasks.generate.common.AbstractGenerateFileTask
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import java.io.File

abstract class GenerateProjektLicenseTask : AbstractGenerateFileTask() {

    override fun getFileText(metadata: ProjektMetadata, repoDirectory: File, file: File): String? {
        val license = metadata.license.mapToModel()
        val licenseTag = SPDX_ID_PREFIX + license.id
        val currentLicenseTag = file.readLines().lastOrNull { it.isNotBlank() }?.trim()
        if (currentLicenseTag == licenseTag) {
            return null
        }
        return buildString {
            append(runBlocking { getLicenseText(metadata, license) })
            appendLine()
            append(licenseTag)
        }
    }

    override fun getOutputFileName(): String = LICENSE_FILE_NAME

    override fun getCommitType(): CommitType = CommitType.DOCS

    private suspend fun getLicenseText(metadata: ProjektMetadata, license: License): String =
        HttpClient(CIO).use { client ->
            val template = client.get(license.templateUrl).bodyAsText()
            license.fillTemplate(template, metadata)
        }

    companion object {
        const val LICENSE_FILE_NAME: String = "LICENSE"
        private const val SPDX_ID_PREFIX: String = "SPDX ID: "
    }
}
