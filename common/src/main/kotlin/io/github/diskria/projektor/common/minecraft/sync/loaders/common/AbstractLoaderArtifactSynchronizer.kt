package io.github.diskria.projektor.common.minecraft.sync.loaders.common

import io.github.diskria.kotlin.utils.extensions.serialization.deserializeFromXml
import io.github.diskria.projektor.common.maven.MavenMetadata
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.common.AbstractMinecraftArtifactSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.common.MinecraftArtifact
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

abstract class AbstractLoaderArtifactSynchronizer : AbstractMinecraftArtifactSynchronizer() {

    abstract val mavenUrl: String

    abstract override val name: String

    abstract override val loader: ModLoaderType

    abstract override val cacheDurationMillis: Long

    abstract fun extractMinecraftVersionString(artifactVersion: String): String?

    override suspend fun loadArtifacts(): List<MinecraftArtifact> =
        loadMavenArtifactVersions().mapNotNull { artifactVersion ->
            val minecraftVersionString = extractMinecraftVersionString(artifactVersion) ?: return@mapNotNull null
            val minecraftVersion = MinecraftVersion.parseOrNull(minecraftVersionString) ?: return@mapNotNull null
            MinecraftArtifact(minecraftVersion, artifactVersion)
        }

    private suspend fun loadMavenArtifactVersions(): List<String> =
        HttpClient(CIO).use { client ->
            client.get(mavenUrl).bodyAsText().deserializeFromXml<MavenMetadata>().versioning.versions.version
        }
}
