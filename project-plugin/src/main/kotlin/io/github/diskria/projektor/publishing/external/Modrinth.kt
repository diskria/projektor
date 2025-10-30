package io.github.diskria.projektor.publishing.external

import com.modrinth.minotaur.TaskModrinthSyncBody
import io.github.diskria.gradle.utils.extensions.findProjectRoot
import io.github.diskria.gradle.utils.extensions.getTask
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.generics.joinBySpace
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.extensions.getJarTask
import io.github.diskria.projektor.extensions.modrinth
import io.github.diskria.projektor.minecraft.loaders.*
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
        val loaderName = loader.getDisplayName()
        val maxSupportedVersion = mod.supportedVersionRange.max.asString()
        modrinth {
            token.set(
                if (EnvironmentHelper.isCI()) Secrets.modrinthToken
                else Constants.Char.EMPTY
            )
            projectId.set(mod.id)
            versionName.set(
                listOf(mod.name, mod.version, "for", loaderName, maxSupportedVersion).joinBySpace()
            )
            versionNumber.set(buildString {
                append(MinecraftMod.SHORT_NAME)
                append(maxSupportedVersion)
                append(Constants.Char.HYPHEN)
                append(mod.version)
                append(Constants.Char.HYPHEN)
                append(loaderName)
            })

            changelog.set(Constants.Char.EMPTY)
            syncBodyFrom.set(
                GenerateProjektReadmeTask.generateText(findProjectRoot(), getProjektMetadata(), isModrinthBody = true)
            )
            when (loader) {
                Fabric -> {
                    with(required) {
                        project("fabric-language-kotlin")
                        if (mod.config.fabric.isApiRequired) {
                            project("fabric-api")
                        }
                    }
                }

                Ornithe -> {
                }

                Forge -> TODO()
                NeoForge -> TODO()
                Quilt -> TODO()
            }

            gameVersions.set(mod.supportedVersionRange.expand().map { it.asString() })
            detectLoaders.set(false)
            loaders.set(listOf(loaderName))

            uploadFile.set(getJarTask())
            debugMode.set(!EnvironmentHelper.isCI())
        }
        val publishTask = tasks.named(publishTaskName).get()
        publishTask.dependsOn(getTask<TaskModrinthSyncBody>())
        return true
    }

    override fun getHomepage(metadata: ProjektMetadata): String =
        buildUrl("modrinth.com") {
            path("mod", metadata.repo.name)
        }

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        ModrinthShield(metadata)
}
