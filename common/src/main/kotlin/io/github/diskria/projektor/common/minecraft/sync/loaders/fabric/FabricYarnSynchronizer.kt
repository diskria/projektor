package io.github.diskria.projektor.common.minecraft.sync.loaders.fabric

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.serialization.deserializeFromXml
import io.github.diskria.kotlin.utils.extensions.splitToPairOrNull
import io.github.diskria.projektor.common.maven.MavenMetadata
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.common.AbstractMinecraftArtifactSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.common.MinecraftArtifact
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.concurrent.TimeUnit

object FabricYarnSynchronizer : AbstractMinecraftArtifactSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.FABRIC

    override val name: String = "yarn"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(7)

    override val isOldestFirst: Boolean = true

    override suspend fun loadArtifacts(): List<MinecraftArtifact> =
        HttpClient(CIO).use { client ->
            val mavenUrl = buildUrl("maven.fabricmc.net") {
                path("net", "fabricmc", "yarn", fileName("maven-metadata", Constants.File.Extension.XML))
            }
            client.get(mavenUrl).bodyAsText()
                .deserializeFromXml<MavenMetadata>()
                .versioning.versions.version
                .mapNotNull { artifactVersion ->
                    val minecraftVersionString =
                        artifactVersion.splitToPairOrNull("+build.")?.first ?: return@mapNotNull null
                    val minecraftVersion =
                        MinecraftVersion.parseOrNull(minecraftVersionString) ?: return@mapNotNull null
                    MinecraftArtifact(minecraftVersion, artifactVersion)
                }
        }
}
