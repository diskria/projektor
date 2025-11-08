package io.github.diskria.projektor.common.minecraft.sync.loaders.legacy_fabric

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.maven.AbstractMinecraftMavenSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.maven.MavenMetadata
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.ktor.http.*
import java.util.concurrent.TimeUnit

object LegacyFabricYarnMappingsSynchronizer : AbstractMinecraftMavenSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.LEGACY_FABRIC

    override val componentName: String = "yarn"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(14)

    override val mavenUrl: Url
        get() = buildUrl("repo.legacyfabric.net") {
            path("legacyfabric", "net", "legacyfabric", "yarn", MavenMetadata.FILE_NAME)
        }

    override fun mapLatestVersion(version: String): String =
        getBuildNumber(version).toString()

    override fun parseMinecraftVersion(version: String): MinecraftVersion? =
        MinecraftVersion.parseOrNull(version.substringBefore(Constants.Char.PLUS))

    override fun parseComponentSemver(version: String): Semver =
        Semver.from(0, 0, getBuildNumber(version))

    private fun getBuildNumber(version: String): Int =
        version.substringAfterLast(Constants.Char.DOT).toInt()
}
