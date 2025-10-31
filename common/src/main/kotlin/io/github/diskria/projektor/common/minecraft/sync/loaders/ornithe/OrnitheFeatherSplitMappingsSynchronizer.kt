package io.github.diskria.projektor.common.minecraft.sync.loaders.ornithe

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.serialization.deserializeFromXml
import io.github.diskria.kotlin.utils.extensions.splitToPairOrNull
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.common.AbstractMinecraftArtifactSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.common.MinecraftArtifact
import io.github.diskria.projektor.common.minecraft.sync.maven.MavenMetadata
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.concurrent.TimeUnit

object OrnitheFeatherSplitMappingsSynchronizer : AbstractMinecraftArtifactSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.ORNITHE

    override val name: String = "feather-split-mappings"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(7)

    override val isOldestFirst: Boolean = true

    private fun fixArtifactVersion(artifactVersion: String): String =
        artifactVersion.splitToPairOrNull("+build.")?.second ?: gradleError("Failed to parse artifact version")

    override suspend fun loadArtifacts(): List<MinecraftArtifact> {
        val artifactVersions = HttpClient(CIO).use { client ->
            val mavenUrl = buildUrl("maven.ornithemc.net") {
                path(
                    "releases",
                    "net",
                    "ornithemc",
                    "feather",
                    fileName("maven-metadata", Constants.File.Extension.XML)
                )
            }
            client.get(mavenUrl).bodyAsText().deserializeFromXml<MavenMetadata>()
        }.versioning.versions.version
        return artifactVersions.mapNotNull { artifactVersion ->
            val (sideVersion, buildNumber) = artifactVersion.splitToPairOrNull("+build.") ?: return@mapNotNull null
            val artifactVersionToFind = when {
                sideVersion.contains("server") -> sideVersion.replace("server", "client")
                sideVersion.contains("client") -> sideVersion.replace("client", "server")
                else -> return@mapNotNull null
            } + "+build.$buildNumber"
            if (!artifactVersions.contains(artifactVersionToFind)) {
                return@mapNotNull null
            }
            val minecraftVersionString = sideVersion.removePrefix("server-").removeSuffix("-server")
                .removePrefix("client-").removeSuffix("-client")
            val minecraftVersion = MinecraftVersion.parseOrNull(minecraftVersionString) ?: return@mapNotNull null
            MinecraftArtifact(minecraftVersion, fixArtifactVersion(artifactVersion))
        }
    }
}
