package io.github.diskria.projektor.common.minecraft.sync.packs

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.minecraft.sync.common.AbstractMinecraftArtifactSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.common.MinecraftArtifact
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

abstract class AbstractPackFormatSynchronizer : AbstractMinecraftArtifactSynchronizer() {

    abstract val wikiTableCaption: String

    final override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(7)

    override suspend fun loadArtifacts(): List<MinecraftArtifact> =
        HttpClient(CIO).use { client ->
            val wikiUrl = buildUrl("minecraft.wiki") {
                path("w", "Pack_format")
            }
            Jsoup.parse(client.get(wikiUrl).bodyAsText())
                .select("caption")
                .firstOrNull { it.ownText().trim() == wikiTableCaption }
                ?.parent()
                ?.select("tr#pack-format-column")
                ?.mapNotNull { tableRow ->
                    val format = tableRow.selectFirst("th#pack-format")?.text()?.trim() ?: return@mapNotNull null
                    val versionRange = tableRow.selectFirst("th#v")?.text()?.trim() ?: return@mapNotNull null
                    val minecraftVersion = MinecraftVersion.parseOrNull(versionRange.substringBefore("–").trim())
                        ?: return@mapNotNull null
                    MinecraftArtifact(minecraftVersion, format)
                }
                ?: gradleError("Failed to parse formats")
        }
}
