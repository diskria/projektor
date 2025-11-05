package io.github.diskria.projektor.common.minecraft.sync.loaders.fabric

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.maven.AbstractMinecraftMavenSynchronizer
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.http.*
import java.util.concurrent.TimeUnit

object FabricYarnMappingsSynchronizer : AbstractMinecraftMavenSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.FABRIC

    override val componentName: String = "yarn"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(7)

    override val mavenUrl: Url =
        buildUrl("maven.fabricmc.net") {
            path("net", "fabricmc", "yarn", fileName("maven-metadata", Constants.File.Extension.XML))
        }

    override fun parseMinecraftVersion(version: String): MinecraftVersion? =
        MinecraftVersion.parseOrNull(version.substringBefore(Constants.Char.PLUS))

    override fun parseComponentSemver(version: String): Semver {
        val build = version.substringAfterLast(Constants.Char.DOT).toInt()
        return Semver.from(0, 0, build)
    }
}
