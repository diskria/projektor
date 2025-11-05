package io.github.diskria.projektor.common.minecraft.sync.loaders.fabric

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.toSemverOrNull
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.loaders.getSupportedVersionRange
import io.github.diskria.projektor.common.minecraft.sync.maven.AbstractMinecraftMavenSynchronizer
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.http.*
import java.util.concurrent.TimeUnit

object FabricLoaderSynchronizer : AbstractMinecraftMavenSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.ORNITHE

    override val componentName: String = "loader"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(7)

    override val mavenUrl: Url =
        buildUrl("maven.fabricmc.net") {
            path("net", "fabricmc", "fabric-loader", fileName("maven-metadata", Constants.File.Extension.XML))
        }

    override fun parseMinecraftVersion(version: String): MinecraftVersion =
        ModLoaderType.ORNITHE.getSupportedVersionRange().min

    override fun parseComponentSemver(version: String): Semver =
        version.toSemverOrNull() ?: Semver.from(0, 0, 0)
}
