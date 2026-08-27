package io.github.diskria.projektor.features.generation.readme.tasks

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.core.model.metadata.ProjektAbout
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.features.distribution.target.mapToModel
import io.github.diskria.projektor.features.generation.readme.MarkdownHelper
import io.github.diskria.projektor.features.generation.readme.shields.static.LicenseShield
import io.github.diskria.projektor.features.generation.tasks.AbstractGenerateFileTask
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "Generates files and performs Git push side effects")
internal abstract class GenerateReadmeTask @Inject constructor(
    providers: ProviderFactory,
    secrets: SecretsHelper,
) : AbstractGenerateFileTask(
    outputFileName = MarkdownHelper.fileName("readme"),
    commitType = CommitType.DOCS,
    providers = providers,
    secrets = secrets,
) {
    @get:Internal
    abstract val projekts: ListProperty<Projekt.Regular>

    @get:Input
    abstract val distributionTargetTypes: ListProperty<DistributionTargetType>

    @get:Input
    abstract val about: Property<ProjektAbout>

    @get:Optional
    @get:Input
    abstract val license: Property<LicenseType>

    override fun getFileText(metadata: ProjektMetadata, repoDirectory: File, file: File): String {
        val licenseModel = license.orNull?.mapToModel()
        val shields = buildList {
            projekts.get().forEach { projekt ->
                distributionTargetTypes.get().forEach { targetType ->
                    targetType.mapToModel().getReadmeShield(projekt)?.let { add(it) }
                }
            }
            licenseModel?.let { add(LicenseShield(licenseModel)) }
        }
        val header = buildString {
            append(MarkdownHelper.header(metadata.displayName, 1))
            append(about.get().description)
            appendLine()
            appendLine()
            append(shields.mapNotNull { it.buildMarkdown() }.joinToString(" "))
        }
        return buildString {
            append(header)
            append(MarkdownHelper.SEPARATOR)
            append(about.get().details)
            licenseModel?.let {
                append(MarkdownHelper.SEPARATOR)
                val licenseLink = MarkdownHelper.link(it.url, "${it.id} License")
                append(MarkdownHelper.header("License", 2))
                append("This project is licensed under the $licenseLink.")
            }
        }
    }
}
