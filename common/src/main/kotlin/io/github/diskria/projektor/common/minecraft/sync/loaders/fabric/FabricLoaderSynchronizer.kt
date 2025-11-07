package io.github.diskria.projektor.common.minecraft.sync.loaders.fabric

import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.toSemverOrNull
import io.github.diskria.projektor.common.minecraft.era.common.MappingsEra
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.loaders.getSupportedVersionRange
import io.github.diskria.projektor.common.minecraft.sync.maven.AbstractMinecraftMavenSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.maven.MavenMetadata
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.ktor.http.*
import org.gradle.api.Project
import java.util.concurrent.TimeUnit

object FabricLoaderSynchronizer : AbstractMinecraftMavenSynchronizer() {

    override val loader: ModLoaderType = ModLoaderType.ORNITHE

    override val mappingsEra: MappingsEra = MappingsEra.CLIENT

    override val componentName: String = "loader"

    override val cacheDurationMillis: Long = TimeUnit.DAYS.toMillis(7)

    override val mavenUrl: Url =
        buildUrl("maven.fabricmc.net") {
            path("net", "fabricmc", "fabric-loader", MavenMetadata.FILE_NAME)
        }

    override fun parseMinecraftVersion(version: String): MinecraftVersion =
        MinecraftVersion.EARLIEST

    override fun parseComponentSemver(version: String): Semver =
        version.toSemverOrNull() ?: Semver.from(0, 0, 0)
}
