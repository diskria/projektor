package io.github.diskria.projektor.features.generation.readme.tasks

import io.github.diskria.projektor.core.model.license.LicenseType
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.core.model.metadata.ProjektAbout
import io.github.diskria.projektor.features.generation.readme.LicenseShield
import io.github.diskria.projektor.features.generation.readme.Markdown
import io.github.diskria.projektor.features.generation.tasks.AbstractGenerateFileTask
import io.github.diskria.projektor.internal.git.CommitType
import io.github.diskria.projektor.internal.utils.DisabledCachingReasons.SIDE_EFFECTS
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.work.DisableCachingByDefault
import java.io.File
import javax.inject.Inject

@DisableCachingByDefault(because = SIDE_EFFECTS)
abstract class GenerateReadmeTask @Inject internal constructor(
    providers: ProviderFactory,
    layout: ProjectLayout,
) : AbstractGenerateFileTask(providers, layout) {

    @get:Input
    abstract val displayName: Property<String>

    @get:Input
    abstract val about: Property<ProjektAbout>

    @get:Optional
    @get:Input
    abstract val licenseType: Property<LicenseType>

    @get:Input
    abstract val distributionTargetShieldMarkdowns: ListProperty<String>

    init {
        fileName.convention("README.md")
        commitType.convention(CommitType.DOCS)
    }

    override fun getFileText(repoDirectory: File, file: File): String {
        val licenseModel = licenseType.orNull?.mapToModel()
        val shields = buildList {
            addAll(distributionTargetShieldMarkdowns.get())
            licenseModel?.let { add(LicenseShield(it).markdown) }
        }
        val header = buildString {
            append(Markdown.header(displayName.get(), 1))
            append(about.get().description)
            if (shields.isNotEmpty()) {
                appendLine()
                appendLine()
                append(shields.joinToString(" "))
            }
        }
        return buildString {
            append(header)
            append(Markdown.SEPARATOR)
            append(about.get().details)
            licenseModel?.let {
                append(Markdown.SEPARATOR)
                append(Markdown.header("License", 2))
                append("This project is licensed under the ${Markdown.link(it.url, "${it.type.id} License")}.")
            }
        }
    }
}
