package io.github.diskria.projektor.common.minecraft.sync.loaders.ornithe

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.era.common.MappingsEra
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.sync.maven.AbstractMinecraftMavenSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.maven.MavenMetadata
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.ktor.http.*
import java.util.concurrent.TimeUnit

class OrnitheFeatherMappingsSynchronizer(override val mappingsEra: MappingsEra) : AbstractMinecraftMavenSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.ORNITHE

    override val componentName: String = "feather-${mappingsEra.getName()}-mappings"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(7)

    override val mavenUrl: Url =
        buildUrl("maven.ornithemc.net") {
            path("releases", "net", "ornithemc", "feather", MavenMetadata.FILE_NAME)
        }

    override fun parseMinecraftVersion(version: String): MinecraftVersion? =
        if (mappingsEra == MappingsEra.MERGED) {
            MinecraftVersion.parseOrNull(version.substringBefore(Constants.Char.PLUS))
        } else {
            val side = if (mappingsEra == MappingsEra.SPLIT) ModSide.SERVER else ModSide.CLIENT
            if (version.contains(side.getName())) {
                MinecraftVersion.parseOrNull(version.substringBefore(Constants.Char.HYPHEN + side.getName()))
            } else {
                null
            }
        }

    override fun mapLatestVersion(version: String): String =
        getBuildNumber(version).toString()

    override fun parseComponentSemver(version: String): Semver =
        Semver.from(0, 0, getBuildNumber(version))

    private fun getBuildNumber(version: String): Int =
        version.substringAfterLast(Constants.Char.DOT).toInt()
}
