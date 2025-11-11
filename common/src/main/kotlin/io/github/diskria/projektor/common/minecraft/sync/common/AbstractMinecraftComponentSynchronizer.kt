package io.github.diskria.projektor.common.minecraft.sync.common

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.rootDirectory
import io.github.diskria.gradle.utils.helpers.GradleDirectories
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.nowMillis
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.serialization.deserializeJsonFromFile
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.kotlin.utils.extensions.toSemver
import io.github.diskria.kotlin.utils.extensions.wrapWithSingleQuote
import io.github.diskria.projektor.ProjektBuildConfig
import io.github.diskria.projektor.common.minecraft.era.common.MappingsType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.common.minecraft.versions.compareTo
import io.github.diskria.projektor.common.minecraft.versions.mappingsType
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import java.io.File

abstract class AbstractMinecraftComponentSynchronizer {

    protected open val loader: ModLoaderType? = null

    protected open val mappingsType: MappingsType? = null

    protected abstract val componentName: String

    protected abstract val cacheDurationMillis: Long

    protected open fun mapLatestVersion(version: String): String = version

    protected open fun parseComponentSemver(version: String): Semver = version.toSemver()

    protected abstract suspend fun fetchComponents(): List<MinecraftComponent>

    fun getLatestComponent(project: Project, minecraftVersion: MinecraftVersion): MinecraftComponent {
        val cacheFile = getCacheFile(project)
        val cache = cacheFile.takeIf { it.exists() }?.deserializeJsonFromFile<MinecraftComponents>()
        val components = cache?.takeIf { nowMillis() - it.lastSyncMillis < cacheDurationMillis } ?: runBlocking {
            val versions = fetchComponents()
                .groupBy { it.minecraftVersion }
                .filterKeys { minecraftVersion ->
                    loader?.let { loader ->
                        loader.supportedVersionRanges.any { it.includesMinecraftVersion(minecraftVersion) } &&
                                (mappingsType == null || mappingsType == minecraftVersion.mappingsType)
                    } ?: true
                }
                .mapValues {
                    val version = it.value.maxBy { version -> parseComponentSemver(version.latestVersion) }
                    version.copy(latestVersion = mapLatestVersion(version.latestVersion))
                }
                .values
                .sortedWith(compareBy(MinecraftVersion.COMPARATOR) { it.minecraftVersion })
            MinecraftComponents(versions, nowMillis()).also { it.serializeJsonToFile(cacheFile.ensureFileExists()) }
        }
        return components.versions
            .filter { it.minecraftVersion <= minecraftVersion }
            .maxWithOrNull(compareBy(MinecraftVersion.COMPARATOR) { it.minecraftVersion })
            ?: gradleError(
                "Latest version of ${componentName.wrapWithSingleQuote()} component " +
                        "not found for Minecraft ${minecraftVersion.asString()}"
            )
    }

    open fun getLatestVersion(project: Project, minecraftVersion: MinecraftVersion): String =
        getLatestComponent(project, minecraftVersion).latestVersion

    private fun getCacheFile(project: Project): File {
        val cacheRoot = project
            .rootDirectory
            .resolve(GradleDirectories.CACHE)
            .resolve(ProjektBuildConfig.LIBRARY_NAME.lowercase())
        val parentDirectory = loader?.let { cacheRoot.resolve(it.getName(`kebab-case`)) } ?: cacheRoot
        return parentDirectory.resolve(fileName("$componentName-versions", Constants.File.Extension.JSON))
    }
}
