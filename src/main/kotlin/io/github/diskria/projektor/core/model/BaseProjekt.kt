package io.github.diskria.projektor.core.model

import io.github.diskria.projektor.api.GradlePluginDsl
import io.github.diskria.projektor.api.KotlinLibraryDsl
import io.github.diskria.projektor.api.ProjektExtension
import io.github.diskria.projektor.core.model.license.License
import io.github.diskria.projektor.core.model.license.mapToModel
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.projektMetadata
import io.github.diskria.projektor.features.publishing.target.PublishingTarget
import io.github.diskria.projektor.features.publishing.target.mapToModel
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

internal data class BaseProjekt(
    override val metadata: ProjektMetadata,
    override val packageName: String,
    override val displayName: String,
    override val version: String,
    override val description: String,
    override val tags: Set<String>,
    override val license: License,
    override val publishingTargets: List<PublishingTarget>,
) : Projekt {

    fun toGradlePlugin(config: GradlePluginDsl): GradlePlugin =
        GradlePlugin(this, config)

    fun toKotlinLibrary(config: KotlinLibraryDsl): KotlinLibrary =
        KotlinLibrary(this, config)

    companion object {
        fun of(project: Project): BaseProjekt {
            val metadata = project.rootProject.projektMetadata
            val extension = project.extensions.getByType<ProjektExtension>()
            return BaseProjekt(
                metadata = metadata,
                packageName = metadata.packageName,
                displayName = metadata.displayName,
                version = metadata.version,
                description = metadata.description,
                tags = metadata.tags,
                license = metadata.license.mapToModel(),
                publishingTargets = extension.publishingTargets.getOrElse(emptyList()).map { it.mapToModel() },
            )
        }
    }
}
