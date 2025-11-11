package io.github.diskria.projektor.publishing.external

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.findGradleProjectRoot
import io.github.diskria.gradle.utils.extensions.hasTask
import io.github.diskria.gradle.utils.extensions.jar
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.extensions.modrinth
import io.github.diskria.projektor.helpers.SecretsHelper
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.live.ModrinthShield
import io.github.diskria.projektor.tasks.generate.GenerateProjektReadmeTask
import io.ktor.http.*
import org.gradle.api.Project

object Modrinth : ExternalPublishingTarget() {

    override val publishTaskName: String = "modrinth"

    override fun configurePublishTask(project: Project, projekt: Projekt): Boolean = with(project) {
        val mod = projekt as? MinecraftMod ?: return false

        val loader = mod.loader
        val loaderName = loader.getLoaderDisplayName()
        val minSupportedVersion = mod.minSupportedVersion.asString()
        val maxSupportedVersion = mod.maxSupportedVersion.asString()
        modrinth {
            token.set(
                if (EnvironmentHelper.isCI()) SecretsHelper.modrinthToken
                else Constants.Char.EMPTY
            )
            projectId.set(mod.repo.name)
            versionName.set(
                buildString {
                    append("${mod.name} ${mod.version} for $loaderName ")
                    if (minSupportedVersion != maxSupportedVersion) {
                        append("$minSupportedVersion ${Constants.Char.EN_DASH} ")
                    }
                    append(maxSupportedVersion)
                }
            )
            versionNumber.set(mod.archiveVersion)

            changelog.set(Constants.Char.EMPTY)
            syncBodyFrom.set(
                GenerateProjektReadmeTask.generateText(
                    findGradleProjectRoot(),
                    getProjektMetadata(),
                    isModrinthBody = true
                )
            )
            gameVersions.set(mod.supportedVersionRange.expand().map { it.asString() })
            detectLoaders.set(false)
            loaders.set(listOf(loaderName))

            uploadFile.set(
                if (hasTask(ShadowJar.SHADOW_JAR_TASK_NAME)) tasks.shadowJar
                else tasks.jar
            )
            debugMode.set(!EnvironmentHelper.isCI())
        }
        val publishTask = tasks.named(publishTaskName).get()
        publishTask.dependsOn("modrinthSyncBody")
        return true
    }

    override fun getHomepage(metadata: ProjektMetadata): Url =
        buildUrl("modrinth.com") {
            path("mod", metadata.repo.name)
        }

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        ModrinthShield(metadata)
}
