package io.github.diskria.projektor.projekt.common

import io.github.diskria.kotlin.utils.extensions.listDirectories
import io.github.diskria.kotlin.utils.extensions.mappers.toEnum
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersionRange
import io.github.diskria.projektor.common.minecraft.versions.common.compareTo
import io.github.diskria.projektor.common.minecraft.versions.common.previousOrNull
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.repo.github.GithubRepo
import io.github.diskria.projektor.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
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
    override val javaVersion: Int,
    override val kotlinVersion: String,
) : Projekt {

    fun toGradlePlugin(project: Project, config: GradlePluginConfiguration): GradlePlugin =
        GradlePlugin(this, config)

    fun toKotlinLibrary(project: Project, config: KotlinLibraryConfiguration): KotlinLibrary =
        KotlinLibrary(this, config)

    fun toAndroidLibrary(project: Project, config: AndroidLibraryConfiguration): AndroidLibrary =
        AndroidLibrary(this, config)

    fun toAndroidApplication(project: Project, config: AndroidApplicationConfiguration): AndroidApplication =
        AndroidApplication(this, config)

    fun toMinecraftMod(project: Project, config: MinecraftModConfiguration): MinecraftMod {
        val minSupportedVersionDirectory = project.projectDir
        val loaderDirectory = minSupportedVersionDirectory.parentFile

        val minSupportedVersion = MinecraftVersion.parse(minSupportedVersionDirectory.name)
        val maxSupportedVersion = config.maxSupportedVersion
            ?: loaderDirectory
                .listDirectories()
                .map { MinecraftVersion.parse(it.name) }
                .filter { it > minSupportedVersion }
                .minWithOrNull(MinecraftVersion.COMPARATOR)
                ?.previousOrNull()
            ?: MinecraftVersion.LATEST

        return MinecraftMod(
            projekt = this,
            config = config,
            loader = loaderDirectory.name.toEnum<ModLoaderType>().mapToModel(),
            supportedVersionRange = MinecraftVersionRange(minSupportedVersion, maxSupportedVersion)
        )
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
                javaVersion = Versions.JAVA,
                kotlinVersion = Versions.KOTLIN,
            )
        }
    }
}
