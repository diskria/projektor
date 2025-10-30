package io.github.diskria.projektor.common.minecraft.sync.loaders.fabric

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.splitToPairOrNull
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.maven.AbstractMinecraftMavenArtifactSynchronizer
import io.ktor.http.*
import java.util.concurrent.TimeUnit

object FabricYarnSynchronizer : AbstractMinecraftMavenArtifactSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.FABRIC

    override val name: String = "yarn"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(7)

    override val mavenUrl: String =
        buildUrl("maven.fabricmc.net") {
            path("net", "fabricmc", "yarn", fileName("maven-metadata", Constants.File.Extension.XML))
        }

    override fun parseMinecraftVersionString(artifactVersion: String): String? =
        artifactVersion.splitToPairOrNull("+build.")?.first
}
