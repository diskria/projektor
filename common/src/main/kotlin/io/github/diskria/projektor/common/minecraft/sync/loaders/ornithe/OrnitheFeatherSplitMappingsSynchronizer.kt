package io.github.diskria.projektor.common.minecraft.sync.loaders.ornithe

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.toSemver
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.maven.AbstractMinecraftMavenSynchronizer
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.ktor.http.*
import java.util.concurrent.TimeUnit

object OrnitheFeatherSplitMappingsSynchronizer : AbstractMinecraftMavenSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.ORNITHE

    override val componentName: String = "feather-split-mappings"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(7)

    override val mavenUrl: Url =
        buildUrl("maven.ornithemc.net") {
            path("releases", "net", "ornithemc", "feather", fileName("maven-metadata", Constants.File.Extension.XML))
        }

    override fun mapLatestVersion(version: String): String =
        version.substringAfterLast(Constants.Char.DOT)

    override fun parseMinecraftVersion(version: String): MinecraftVersion? =
        if (version.contains("server")) MinecraftVersion.parseOrNull(version.substringBefore(Constants.Char.HYPHEN))
        else null

    override fun parseComponentSemver(version: String): Semver {
        val build = version.substringAfterLast(Constants.Char.DOT).toInt()
        return Semver.from(0, 0, build)
    }
}
