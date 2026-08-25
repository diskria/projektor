package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.core.model.license.License
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.ProjektorHttpClient
import io.github.diskria.projektor.internal.utils.SecretsHelper
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.provider.ProviderFactory
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "Generates files and performs Git push side effects")
internal abstract class GenerateLicenseTask @Inject constructor(
    providers: ProviderFactory,
    secrets: SecretsHelper,
) : AbstractGenerateFileTask(
    outputFileName = "LICENSE",
    commitType = CommitType.DOCS,
    providers = providers,
    secrets = secrets,
) {
    override fun getFileText(metadata: ProjektMetadata, repoDirectory: File, file: File): String? {
        val license = metadata.license.mapToModel()
        val spdxTag = "SPDX ID: ${license.id}"
        val currentLicenseTag = file.readLines().lastOrNull { it.isNotBlank() }?.trim()
        if (currentLicenseTag == spdxTag) {
            return null
        }
        return buildString {
            append(runBlocking { getLicenseText(metadata, license) })
            appendLine()
            append(spdxTag)
        }
    }

    private suspend fun getLicenseText(metadata: ProjektMetadata, license: License): String {
        val template = ProjektorHttpClient.client.get(license.templateUrl).bodyAsText()
        return license.fillTemplate(template, metadata)
    }
}
