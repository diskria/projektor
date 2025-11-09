package io.github.diskria.projektor.projekt.common

import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.snake_case
import io.github.diskria.kotlin.utils.extensions.listDirectories
import io.github.diskria.kotlin.utils.extensions.mappers.toEnum
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.compareTo
import io.github.diskria.projektor.common.minecraft.versions.previousOrNull
import io.github.diskria.projektor.common.minecraft.versions.rangeTo
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.repo.github.GithubRepo
import io.github.diskria.projektor.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.extensions.mappers.mapToModel
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.projekt.*
import io.github.diskria.projektor.publishing.common.PublishingTarget
import org.gradle.api.Project

data class BaseProjekt(
    override val metadata: ProjektMetadata,
    override val type: ProjektType,
    override val repo: GithubRepo,
    override val packageNameBase: String,
    override val name: String,
    override val version: String,
    override val description: String,
    override val tags: Set<String>,
    override val license: License,
    override val publishingTargets: List<PublishingTarget>,
) : Projekt {

    fun toGradlePlugin(config: GradlePluginConfiguration): GradlePlugin =
        GradlePlugin(this, config)

    fun toKotlinLibrary(config: KotlinLibraryConfiguration): KotlinLibrary =
        KotlinLibrary(this, config)

    fun toAndroidLibrary(config: AndroidLibraryConfiguration): AndroidLibrary =
        AndroidLibrary(this, config)

    fun toAndroidApplication(config: AndroidApplicationConfiguration): AndroidApplication =
        AndroidApplication(this, config)

    fun toMinecraftMod(project: Project, config: MinecraftModConfiguration): MinecraftMod {
        val minSupportedVersionDirectory = project.projectDir
        val loaderDirectory = minSupportedVersionDirectory.parentFile

        val loader = loaderDirectory.name.setCase(`kebab-case`, snake_case).toEnum<ModLoaderType>().mapToModel()
        val minSupportedVersion = MinecraftVersion.parse(minSupportedVersionDirectory.name)
        config.resolveConfig(loader, project, minSupportedVersion)

        val maxSupportedVersion = config.maxSupportedVersion
            ?: run {
                val minecraftVersions = loaderDirectory
                    .listDirectories()
                    .mapNotNull { MinecraftVersion.parseOrNull(it.name) }
                val nextMinSupportedVersion = minecraftVersions
                    .filter { it > minSupportedVersion }
                    .minWithOrNull(MinecraftVersion.COMPARATOR)
                return@run nextMinSupportedVersion?.previousOrNull()
            }
            ?: run {
                val supportedRanges = loader.mapToEnum().supportedVersionRanges
                supportedRanges.maxWith(compareBy(MinecraftVersion.COMPARATOR) { it.max }).max
            }
        return MinecraftMod(this, config, loader, minSupportedVersion..maxSupportedVersion)
    }

    companion object {
        fun of(project: Project): BaseProjekt {
            val metadata = project.getProjektMetadata()
            return BaseProjekt(
                metadata = metadata,
                type = metadata.type,
                repo = metadata.repo,
                packageNameBase = metadata.packageNameBase,
                name = metadata.name,
                version = metadata.version,
                description = metadata.description,
                tags = metadata.tags,
                license = metadata.license.mapToModel(),
                publishingTargets = metadata.publishingTargets.map { it.mapToModel() },
            )
        }
    }
}
