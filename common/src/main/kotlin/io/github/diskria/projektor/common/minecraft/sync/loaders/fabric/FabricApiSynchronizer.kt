package io.github.diskria.projektor.common.minecraft.sync.loaders.fabric

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.serialization.deserializeFromJson
import io.github.diskria.kotlin.utils.serialization.annotations.IgnoreUnknownKeys
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.common.AbstractMinecraftArtifactSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.common.MinecraftArtifact
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

object FabricApiSynchronizer : AbstractMinecraftArtifactSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.FABRIC

    override val name: String = "api"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(1)

    override suspend fun loadArtifacts(): List<MinecraftArtifact> =
        HttpClient(CIO).use { client ->
            val modrinthApiUrl = buildUrl("api.modrinth.com") {
                path("v2", "project", "fabric-api", "version")
                parameters.append("version_type", "release")
            }
            client.get(modrinthApiUrl).bodyAsText()
                .deserializeFromJson<ModrinthVersionsResponse>()
                .response
                .mapNotNull { modrinthVersion ->
                    val minMinecraftVersion = modrinthVersion.minecraftVersions
                        .mapNotNull { MinecraftVersion.parseOrNull(it) }
                        .minWithOrNull(MinecraftVersion.COMPARATOR) ?: return@mapNotNull null
                    MinecraftArtifact(minMinecraftVersion, modrinthVersion.versionNumber)
                }
        }

    @JvmInline
    @Serializable
    @IgnoreUnknownKeys
    value class ModrinthVersionsResponse(val response: List<ModrinthVersion>)

    @Serializable
    data class ModrinthVersion(
        @SerialName("game_versions")
        val minecraftVersions: List<String>,

        @SerialName("version_number")
        val versionNumber: String,
    )
}
