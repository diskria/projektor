package io.github.diskria.projektor.features.generation.readme.tasks

import io.github.diskria.projektor.core.model.PublishingTargetType
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.core.model.metadata.ProjektAbout
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.features.generation.readme.MarkdownHelper
import io.github.diskria.projektor.features.generation.readme.shields.static.LicenseShield
import io.github.diskria.projektor.features.generation.tasks.AbstractGenerateFileTask
import io.github.diskria.projektor.features.publishing.target.mapToModel
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.SecretsHelper
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = "Generates files and performs Git push side effects")
internal abstract class GenerateProjektReadmeTask @Inject constructor(
    providers: ProviderFactory,
    secrets: SecretsHelper,
) : AbstractGenerateFileTask(
    outputFileName = MarkdownHelper.fileName("readme"),
    commitType = CommitType.DOCS,
    providers = providers,
    secrets = secrets,
) {
    @get:Input
    abstract val publishingTargets: ListProperty<PublishingTargetType>

    override fun getFileText(metadata: ProjektMetadata, repoDirectory: File, file: File): String {
        val about = ProjektAbout.of(repoDirectory)
        val shields = buildList {
            addAll(publishingTargets.getOrElse(emptyList()).sorted().mapNotNull {
                it.mapToModel().getReadmeShield(metadata)
            })
            add(LicenseShield(metadata.license.mapToModel()))
        }
        val header = buildString {
            append(MarkdownHelper.header(metadata.name, 1))
            append(about.description)
            appendLine()
            appendLine()
            append(shields.joinToString(" ") { it.buildMarkdown() })
        }
        val footer = buildString {
            val license = metadata.license.mapToModel()
            val licenseLink = MarkdownHelper.link(license.url, "${license.id} License")
            append(MarkdownHelper.header("License", 2))
            append("This project is licensed under the $licenseLink.")
        }
        return buildString {
            append(header)
            append(MarkdownHelper.SEPARATOR)
            append(about.details)
            append(MarkdownHelper.SEPARATOR)
            append(footer)
        }
    }
}
