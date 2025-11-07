package io.github.diskria.projektor.publishing.external

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.findGradleProjectRoot
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.extensions.modrinth
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.live.ModrinthShield
import io.github.diskria.projektor.tasks.generate.GenerateProjektReadmeTask
import io.ktor.http.*
import org.gradle.api.Project

data object Modrinth : ExternalPublishingTarget() {

    override val publishTaskName: String = "modrinth"

    override fun configurePublishTask(projekt: Projekt, project: Project): Boolean = with(project) {
        val mod = projekt as? MinecraftMod ?: return false

        val loader = mod.loader
        val loaderName = loader.getLoaderDisplayName()
        val minSupportedVersion = mod.minSupportedVersion.asString()
        val maxSupportedVersion = mod.maxSupportedVersion.asString()
        modrinth {
            token.set(
                if (EnvironmentHelper.isCI()) Secrets.modrinthToken
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

            uploadFile.set(tasks.shadowJar)
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
