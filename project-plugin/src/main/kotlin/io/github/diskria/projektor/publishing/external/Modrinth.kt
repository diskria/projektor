package io.github.diskria.projektor.publishing.external

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import com.modrinth.minotaur.TaskModrinthSyncBody
import io.github.diskria.gradle.utils.extensions.findProjectRoot
import io.github.diskria.gradle.utils.extensions.getTask
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.failWithUnsupportedType
import io.github.diskria.kotlin.utils.extensions.generics.joinBySpace
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.extensions.modrinth
import io.github.diskria.projektor.minecraft.loaders.fabric.Fabric
import io.github.diskria.projektor.minecraft.loaders.fabric.ornithe.Ornithe
import io.github.diskria.projektor.minecraft.loaders.fabric.quilt.Quilt
import io.github.diskria.projektor.minecraft.loaders.forge.Forge
import io.github.diskria.projektor.minecraft.loaders.forge.neoforge.NeoForge
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
        val minSupportedVersion = mod.supportedVersionRange.min.asString()
        val maxSupportedVersion = mod.supportedVersionRange.max.asString()
        modrinth {
            token.set(
                if (EnvironmentHelper.isCI()) Secrets.modrinthToken
                else Constants.Char.EMPTY
            )
            projectId.set(mod.id)
            versionName.set(
                listOf(
                    mod.name,
                    mod.version,
                    "for",
                    loaderName,
                    minSupportedVersion,
                    Constants.Char.EN_DASH,
                    maxSupportedVersion
                ).joinBySpace()
            )
            versionNumber.set(mod.archiveVersion)

            changelog.set(Constants.Char.EMPTY)
            syncBodyFrom.set(
                GenerateProjektReadmeTask.generateText(findProjectRoot(), getProjektMetadata(), isModrinthBody = true)
            )
            when (loader) {
                Fabric -> {
                    required.project("fabric-api", "fabric-language-kotlin")
                }

                Ornithe -> {
                    required.project("osl")
                }

                NeoForge -> {

                }

                Forge -> TODO()
                Quilt -> TODO()
                else -> failWithUnsupportedType(loader::class)
            }

            gameVersions.set(mod.supportedVersionRange.expand().map { it.asString() })
            detectLoaders.set(false)
            loaders.set(listOf(loaderName))

            uploadFile.set(tasks.shadowJar)
            debugMode.set(!EnvironmentHelper.isCI())
        }
        val publishTask = tasks.named(publishTaskName).get()
        publishTask.dependsOn(getTask<TaskModrinthSyncBody>())
        return true
    }

    override fun getHomepage(metadata: ProjektMetadata): Url =
        buildUrl("modrinth.com") {
            path("mod", metadata.repo.name)
        }

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        ModrinthShield(metadata)
}
