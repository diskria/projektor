package io.github.diskria.projektor.common.minecraft.sync.loaders.fabric

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.splitToPairOrNull
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.loaders.common.AbstractLoaderArtifactSynchronizer
import io.ktor.http.*
import java.util.concurrent.TimeUnit

object FabricApiSynchronizer : AbstractLoaderArtifactSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.FABRIC

    override val name: String = "api"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(1)

    override val mavenUrl: String =
        buildUrl("api.modrinth.com") {
            path("maven", "maven", "modrinth", "fabric-api", fileName("maven-metadata", Constants.File.Extension.XML))
        }

    override fun extractMinecraftVersionString(artifactVersion: String): String? =
        artifactVersion.splitToPairOrNull("+")?.second
}
