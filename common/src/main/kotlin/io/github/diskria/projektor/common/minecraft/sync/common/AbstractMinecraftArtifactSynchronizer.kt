package io.github.diskria.projektor.common.minecraft.sync.common

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.nowMillis
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.serialization.deserializeJsonFromFile
import io.github.diskria.kotlin.utils.extensions.serialization.serializeJsonToFile
import io.github.diskria.kotlin.utils.extensions.wrapWithSingleQuote
import io.github.diskria.projektor.ProjektBuildConfig
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.common.minecraft.versions.common.compareTo
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import java.io.File

abstract class AbstractMinecraftArtifactSynchronizer {

    abstract val name: String

    abstract val cacheDurationMillis: Long

    open val loader: ModLoaderType? = null

    abstract suspend fun loadArtifacts(): List<MinecraftArtifact>

    fun sync(settings: Settings) {
        val outputFile = getOutputFile(settings.rootDir)
        if (outputFile.exists()) {
            val lastSyncMillis = outputFile.deserializeJsonFromFile<MinecraftArtifacts>().lastSyncMillis
            if (nowMillis() - lastSyncMillis < cacheDurationMillis) {
                return
            }
        }
        runBlocking {
            val artifacts = loadArtifacts()
                .sortedWith(compareBy(MinecraftVersion.COMPARATOR.reversed()) { it.minecraftVersion })
            MinecraftArtifacts(artifacts, nowMillis()).serializeJsonToFile(outputFile.ensureFileExists())
        }
    }

    fun getArtifactVersion(project: Project, minecraftVersion: MinecraftVersion): String =
        getOutputFile(project.rootDir)
            .deserializeJsonFromFile<MinecraftArtifacts>()
            .artifacts
            .firstOrNull { it.minecraftVersion <= minecraftVersion }
            ?.artifactVersion
            ?: gradleError(
                "Artifact ${name.wrapWithSingleQuote()} not found for Minecraft version ${minecraftVersion.asString()}"
            )

    private fun getOutputFile(rootDirectory: File): File {
        val cacheDirectory = rootDirectory
            .resolve(ProjectDirectories.GRADLE_CACHE)
            .resolve(ProjektBuildConfig.LIBRARY_NAME.lowercase())
        val fileParentDirectory = loader?.let { cacheDirectory.resolve(it.getName()) } ?: cacheDirectory
        return fileParentDirectory.resolve(fileName("$name-versions", Constants.File.Extension.JSON))
    }
}
