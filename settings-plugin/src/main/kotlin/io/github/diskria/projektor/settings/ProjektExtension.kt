package io.github.diskria.projektor.settings

import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.common.extensions.ConfigurationExtension
import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.projekt.metadata.AboutMetadata
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.metadata.github.GithubRepository
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : ConfigurationExtension<ProjektType>() {

    val version: Property<String> = objects.property(String::class.java)
    val license: Property<LicenseType> = objects.property(LicenseType::class.java)
    val publish: Property<PublishingTargetType> = objects.property(PublishingTargetType::class.java)

    val versionCatalogPath: Property<String> = objects.property(String::class.java)
    val extraRepositories: SetProperty<ProjektType> = objects.setProperty(ProjektType::class.java)

    fun gradlePlugin() {
        configure(ProjektType.GRADLE_PLUGIN)
    }

    fun kotlinLibrary() {
        configure(ProjektType.KOTLIN_LIBRARY)
    }

    fun androidLibrary() {
        configure(ProjektType.ANDROID_LIBRARY)
    }

    fun androidApplication() {
        configure(ProjektType.ANDROID_APPLICATION)
    }

    fun minecraftMod() {
        configure(ProjektType.MINECRAFT_MOD)
    }

    fun buildMetadata(type: ProjektType, repository: GithubRepository, about: AboutMetadata): ProjektMetadata =
        ProjektMetadata(
            type = type,
            repository = repository,
            name = repository.name.setCase(`kebab-case`, `Title Case`),
            description = about.description,
            version = requireProperty(version, ::version.name),
            license = requireProperty(license, ::license.name),
            publishingTarget = requireProperty(publish, ::publish.name),
            tags = about.tags,
        )
}
