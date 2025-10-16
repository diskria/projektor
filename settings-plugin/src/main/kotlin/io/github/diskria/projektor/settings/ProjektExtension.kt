package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.GradleExtension
import io.github.diskria.gradle.utils.extensions.isCI
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.properties.AutoNamedEnvironmentVariable
import io.github.diskria.projektor.common.licenses.LicenseType
import io.github.diskria.projektor.common.projekt.ProjektMetadata
import io.github.diskria.projektor.common.projekt.ProjektType
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

    fun onTypeReady(callback: (ProjektType) -> Unit) {
        onTypeReadyCallback = callback
    }

    fun gradlePlugin() {
        setType(ProjektType.GRADLE_PLUGIN)
    }

    fun kotlinLibrary() {
        setType(ProjektType.KOTLIN_LIBRARY)
    }

    fun androidLibrary() {
        setType(ProjektType.ANDROID_LIBRARY)
    }

    fun androidApplication() {
        setType(ProjektType.ANDROID_APPLICATION)
    }

    fun minecraftMod() {
        setType(ProjektType.MINECRAFT_MOD)
    }

    fun buildMetadata(settings: Settings, type: ProjektType): ProjektMetadata = with(settings) {
        val (owner, repo) = if (providers.isCI) {
            val githubOwner by AutoNamedEnvironmentVariable(isRequired = true)
            val githubRepo by AutoNamedEnvironmentVariable(isRequired = true)
            githubOwner to githubRepo
        } else {
            val owner = rootDir.parentFile.asDirectory().name
            val repo = rootDir.name
            owner to repo
        }
        val repoDirectory = settings.rootDir
        val aboutDirectory = repoDirectory.resolve("about")
        val tagsFile = aboutDirectory.resolve("TAGS.md")
        if (!tagsFile.exists()) {
            tagsFile.createNewFile()
        }
        val tags = tagsFile.readLines().filter { it.isBlank() }.toSet().ifEmpty {
            gradleError("File $tagsFile is empty. You must to describe some tags that most relative to projekt idea.")
        }

        val englishAboutDirectory = aboutDirectory.resolve("en")
        englishAboutDirectory.mkdirs()

        val descriptionFile = englishAboutDirectory.resolve("DESCRIPTION.md")
        if (!descriptionFile.exists()) {
            descriptionFile.createNewFile()
        }
        val description = descriptionFile.readText().trim().ifEmpty {
            gradleError("File $descriptionFile is empty. You must to describe in him short description of projekt")
        }

        ProjektMetadata(
            type = type,
            owner = owner,
            developer = owner.substringBefore(Constants.Char.HYPHEN),
            email = "diskria@proton.me",
            repo = repo,
            name = repo.setCase(`kebab-case`, `Title Case`),
            description = description,
            version = requireProperty(version, ::version.name),
            license = requireProperty(license, ::license.name),
            publishingTarget = requireProperty(publish, ::publish.name),
            tags = tags,
        )
    }

    fun checkNotConfigured() {
        if (type == null) {
            gradleError("Projekt type not configured!")
        }
    }

    private fun setType(type: ProjektType) {
        if (this.type != null) {
            gradleError("Projekt type already configured!")
        }
        this.type = type
        onTypeReadyCallback?.invoke(type)
    }
}
