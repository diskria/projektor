package io.github.diskria.projektor.common.minecraft.sync.loaders.ornithe

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.splitToPairOrNull
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.maven.AbstractMinecraftMavenArtifactSynchronizer
import io.ktor.http.*
import java.util.concurrent.TimeUnit

object OrnitheFeatherSynchronizer : AbstractMinecraftMavenArtifactSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.ORNITHE

    override val name: String = "feather"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(7)

    override val isOldestFirst: Boolean = true

    override val mavenUrl: String =
        buildUrl("maven.ornithemc.net") {
            path("releases", "net", "ornithemc", "feather", fileName("maven-metadata", Constants.File.Extension.XML))
        }

    override fun parseMinecraftVersionString(artifactVersion: String): String? =
        artifactVersion.splitToPairOrNull("+build.")?.first

    override fun fixArtifactVersion(artifactVersion: String): String =
        artifactVersion.splitToPairOrNull("+build.")?.second ?: gradleError("Failed to parse artifact version")
}
