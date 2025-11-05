package io.github.diskria.projektor.common.minecraft.sync.maven

import io.github.diskria.kotlin.utils.extensions.serialization.deserializeFromXml
import io.github.diskria.projektor.common.minecraft.sync.common.AbstractMinecraftComponentSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.common.MinecraftComponent
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

abstract class AbstractMinecraftMavenSynchronizer : AbstractMinecraftComponentSynchronizer() {

    abstract val mavenUrl: Url

    abstract fun parseMinecraftVersion(version: String): MinecraftVersion?

    override suspend fun fetchComponents(): List<MinecraftComponent> =
        HttpClient(CIO).use { client ->
            val mavenMetadata = client.get(mavenUrl).bodyAsText().deserializeFromXml<MavenMetadata>()
            val versions = mavenMetadata.versioning.versions.version
            versions.mapNotNull { version ->
                val minecraftVersion = parseMinecraftVersion(version) ?: return@mapNotNull null
                MinecraftComponent(minecraftVersion, version)
            }
        }
}
