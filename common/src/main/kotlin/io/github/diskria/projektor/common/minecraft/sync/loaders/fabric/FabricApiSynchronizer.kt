package io.github.diskria.projektor.common.minecraft.sync.loaders.fabric

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.serialization.deserializeFromJson
import io.github.diskria.kotlin.utils.extensions.toSemver
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.common.AbstractMinecraftComponentSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.common.MinecraftComponent
import io.github.diskria.projektor.common.minecraft.sync.modrinth.ModrinthResponse
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.concurrent.TimeUnit

object FabricApiSynchronizer : AbstractMinecraftComponentSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.FABRIC

    override val componentName: String = "api"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(1)

    override suspend fun fetchComponents(): List<MinecraftComponent> =
        HttpClient(CIO).use { client ->
            val modrinthApiUrl = buildUrl("api.modrinth.com") {
                path("v2", "project", "fabric-api", "version")
                parameters.append("version_type", "release")
            }
            val versions = client.get(modrinthApiUrl).bodyAsText().deserializeFromJson<ModrinthResponse>().versions
            versions.mapNotNull { version ->
                val minSupportedVersion = version.supportedMinecraftVersions
                    .mapNotNull { MinecraftVersion.parseOrNull(it) }
                    .minWithOrNull(MinecraftVersion.COMPARATOR) ?: return@mapNotNull null
                MinecraftComponent(minSupportedVersion, version.versionNumber)
            }
        }

    override fun parseComponentSemver(version: String): Semver =
        version.substringBefore(Constants.Char.PLUS).toSemver()
}
