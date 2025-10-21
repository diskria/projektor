package io.github.diskria.projektor.settings.extensions.gradle

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.`dot․case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.common.extensions.gradle.AbstractProjektExtension
import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.metadata.ProjektAbout
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.common.publishing.PublishingTargetType
import io.github.diskria.projektor.common.repo.github.GithubRepo
import io.github.diskria.projektor.settings.configurations.*
import io.github.diskria.projektor.settings.configurators.*
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

open class ProjektExtension @Inject constructor(
    objects: ObjectFactory
) : AbstractProjektExtension<SettingsConfigurator>() {

    val version: Property<String> = objects.property(String::class.java)
    val license: Property<LicenseType> = objects.property(LicenseType::class.java)
    val publish: SetProperty<PublishingTargetType> = objects.setProperty(PublishingTargetType::class.java)

    private var projektType: ProjektType? = null

    fun buildMetadata(repo: GithubRepo, about: ProjektAbout): ProjektMetadata =
        ProjektMetadata(
            type = projektType ?: gradleError("Projekt type not initialized"),
            repo = repo,
            packageNameBase = repo.owner.namespace.appendPackageName(
                repo.name.setCase(`kebab-case`, `dot․case`)
            ),
            name = repo.name.setCase(`kebab-case`, `Title Case`),
            version = requireProperty(version, ::version.name),
            license = requireProperty(license, ::license.name),
            publishingTargets = requireProperty(publish, ::publish.name).ifEmpty {
                gradleError("Projekt must be configured for at least one publishing target")
            },
            description = about.description,
            tags = about.tags,
        )

    fun gradlePlugin(configuration: GradlePluginConfiguration.() -> Unit = {}) {
        projektType = ProjektType.GRADLE_PLUGIN
        setConfigurator(GradlePluginConfigurator(GradlePluginConfiguration().apply(configuration)))
    }

    fun kotlinLibrary(configuration: KotlinLibraryConfiguration.() -> Unit = {}) {
        projektType = ProjektType.KOTLIN_LIBRARY
        setConfigurator(KotlinLibraryConfigurator(KotlinLibraryConfiguration().apply(configuration)))
    }

    fun androidLibrary(configuration: AndroidLibraryConfiguration.() -> Unit = {}) {
        projektType = ProjektType.ANDROID_LIBRARY
        setConfigurator(AndroidLibraryConfigurator(AndroidLibraryConfiguration().apply(configuration)))
    }

    fun androidApplication(configuration: AndroidApplicationConfiguration.() -> Unit = {}) {
        projektType = ProjektType.ANDROID_APPLICATION
        setConfigurator(AndroidApplicationConfigurator(AndroidApplicationConfiguration().apply(configuration)))
    }

    fun minecraftMod(configuration: MinecraftModConfiguration.() -> Unit = {}) {
        projektType = ProjektType.MINECRAFT_MOD
        setConfigurator(MinecraftModConfigurator(MinecraftModConfiguration().apply(configuration)))
    }
}
