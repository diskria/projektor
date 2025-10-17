package io.github.diskria.projektor.projekt.common

import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.getMetadata
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.publishing.common.PublishingTarget
import org.gradle.api.Project

data class Projekt(
    override val metadata: ProjektMetadata,
    override val license: License,
    override val publishingTarget: PublishingTarget,
    override val javaVersion: Int,
    override val kotlinVersion: String,
) : IProjekt {

    companion object {
        fun of(project: Project): Projekt {
            val metadata = project.getMetadata()
            return Projekt(
                metadata = metadata,
                license = metadata.license.mapToModel(),
                publishingTarget = metadata.publishingTarget.mapToModel(),
                javaVersion = Versions.JAVA,
                kotlinVersion = Versions.KOTLIN,
            )
        }
    }
}
