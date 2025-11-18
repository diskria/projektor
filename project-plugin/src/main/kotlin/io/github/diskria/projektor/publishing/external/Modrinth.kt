package io.github.diskria.projektor.publishing.external

import io.github.diskria.gradle.utils.extensions.getTaskOrNull
import io.github.diskria.gradle.utils.extensions.jar
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.extensions.modrinth
import io.github.diskria.projektor.helpers.SecretsHelper
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.live.ModrinthShield
import io.github.diskria.projektor.tasks.minecraft.ZipMultiSideMinecraftModTask
import io.ktor.http.*
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign

object Modrinth : ExternalPublishingTarget() {

    override val publishTaskName: String = "modrinth"

    override fun configurePublishTask(project: Project, projekt: Projekt): Boolean = with(project) {
        val mod = projekt as? MinecraftMod ?: return false

        val loader = mod.loader
        val loaderName = loader.mapToEnum().displayName
        val minSupportedVersion = mod.minSupportedVersion.asString()
        val maxSupportedVersion = mod.maxSupportedVersion.asString()
        modrinth {
            val isDebugModeEnabled = !EnvironmentHelper.isCI()
            token = when {
                isDebugModeEnabled -> Constants.Char.EMPTY
                else -> SecretsHelper.modrinthToken
            }

            debugMode = isDebugModeEnabled
            uploadFile = getTaskOrNull<ZipMultiSideMinecraftModTask>() ?: tasks.jar

            projectId = mod.repo.name
            versionName = buildString {
                append("${mod.name} ${mod.version} for $loaderName ")
                if (minSupportedVersion != maxSupportedVersion) {
                    append("$minSupportedVersion ${Constants.Char.EN_DASH} ")
                }
                append(maxSupportedVersion)
            }
            versionNumber = mod.archiveVersion
            gameVersions = mod.supportedVersionRange.expand().map { it.asString() }
            loaders = listOf(loaderName)

            detectLoaders = false
            changelog = Constants.Char.EMPTY
        }
        return true
    }

    override fun getHomepage(metadata: ProjektMetadata): Url =
        buildUrl("modrinth.com") {
            path("mod", metadata.repo.name)
        }

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        ModrinthShield(metadata)
}
