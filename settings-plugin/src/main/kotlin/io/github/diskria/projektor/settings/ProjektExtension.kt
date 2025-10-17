package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.GradleExtension
import io.github.diskria.gradle.utils.extensions.isCI
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.properties.common.autoNamed
import io.github.diskria.kotlin.utils.properties.common.environmentVariable
import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.projekt.OwnerType
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.projekt.metadata.AboutMetadata
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.metadata.github.GithubOwner
import io.github.diskria.projektor.common.projekt.metadata.github.GithubRepository
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : GradleExtension() {

    val version: Property<String> = objects.property(String::class.java)
    val license: Property<LicenseType> = objects.property(LicenseType::class.java)
    val publish: Property<PublishingTargetType> = objects.property(PublishingTargetType::class.java)

    val versionCatalogPath: Property<String> = objects.property(String::class.java)
    val extraRepositories: SetProperty<ProjektType> = objects.setProperty(ProjektType::class.java)

    private var type: ProjektType? = null
    private var onTypeReadyCallback: ((ProjektType) -> Unit)? = null

    fun onConfigured(callback: (ProjektType) -> Unit) {
        onTypeReadyCallback = callback
    }

    fun gradlePlugin() {
        setProjektType(ProjektType.GRADLE_PLUGIN)
    }

    fun kotlinLibrary() {
        setProjektType(ProjektType.KOTLIN_LIBRARY)
    }

    fun androidLibrary() {
        setProjektType(ProjektType.ANDROID_LIBRARY)
    }

    fun androidApplication() {
        setProjektType(ProjektType.ANDROID_APPLICATION)
    }

    fun minecraftMod() {
        setProjektType(ProjektType.MINECRAFT_MOD)
    }

    fun buildMetadata(settings: Settings, type: ProjektType): ProjektMetadata = with(settings) {
        val (ownerName, repositoryName) = if (providers.isCI) {
            val githubOwner by autoNamed.environmentVariable(isRequired = true)
            val githubRepo by autoNamed.environmentVariable(isRequired = true)
            githubOwner to githubRepo
        } else {
            val ownerName = rootDir.parentFile.asDirectory().name
            val repositoryName = rootDir.name
            ownerName to repositoryName
        }
        val ownerType = when {
            ownerName.first().isUpperCase() -> OwnerType.BRAND
            ownerName.contains(Constants.Char.HYPHEN) -> OwnerType.DOMAIN
            else -> OwnerType.PROFILE
        }
        val about = AboutMetadata.of(rootDir)
        ProjektMetadata(
            type = type,
            repository = GithubRepository(
                GithubOwner(ownerType, ownerName, "diskria@proton.me"),
                repositoryName
            ),
            name = repositoryName.setCase(`kebab-case`, `Title Case`),
            description = about.description,
            version = requireProperty(version, ::version.name),
            license = requireProperty(license, ::license.name),
            publishingTarget = requireProperty(publish, ::publish.name),
            tags = about.tags,
        )
    }

    fun ensureConfigured() {
        if (type == null) {
            gradleError("Projekt not configured!")
        }
    }

    private fun setProjektType(type: ProjektType) {
        if (this.type != null) {
            gradleError("Projekt already configured!")
        }
        this.type = type
        onTypeReadyCallback?.invoke(type)
    }
}
