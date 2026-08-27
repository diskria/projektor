package io.github.diskria.projektor.features.generation.tasks

import io.github.diskria.projektor.core.model.license.License
import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.ProjektorHttpClient
import io.github.diskria.projektor.internal.utils.SecretsHelper
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
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
    @get:Input
    abstract val licenseType: Property<LicenseType>

    @get:Input
    abstract val developer: Property<String>

    override fun getFileText(repoDirectory: File, file: File): String? =
        runBlocking { getLicenseText(developer.get(), licenseType.get().mapToModel()) }

    private suspend fun getLicenseText(developer: String, license: License): String =
        license.fillTemplate(ProjektorHttpClient.client.get(license.templateUrl).bodyAsText(), developer)
}
