package io.github.diskria.projektor.common.minecraft.sync.maven

import io.github.diskria.kotlin.utils.extensions.serialization.deserializeFromXml
import io.github.diskria.projektor.common.minecraft.sync.common.AbstractMinecraftArtifactSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.common.MinecraftArtifact
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

abstract class AbstractMinecraftMavenArtifactSynchronizer : AbstractMinecraftArtifactSynchronizer() {

    abstract val mavenUrl: String

    abstract fun parseMinecraftVersionString(artifactVersion: String): String?

    open fun fixArtifactVersion(artifactVersion: String): String =
        artifactVersion

    override val isOldestFirst: Boolean = true

    override suspend fun loadArtifacts(): List<MinecraftArtifact> =
        HttpClient(CIO).use { client ->
            client.get(mavenUrl).bodyAsText().deserializeFromXml<MavenMetadata>()
        }.versioning.versions.version.mapNotNull { artifactVersion ->
            val minecraftVersionString = parseMinecraftVersionString(artifactVersion) ?: return@mapNotNull null
            val minecraftVersion = MinecraftVersion.parseOrNull(minecraftVersionString) ?: return@mapNotNull null
            MinecraftArtifact(minecraftVersion, fixArtifactVersion(artifactVersion))
        }
}
