package io.github.diskria.projektor.settings

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.SettingsExtension
import io.github.diskria.gradle.utils.extensions.isCI
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.kotlin.utils.extensions.toSemver
import io.github.diskria.kotlin.utils.properties.AutoNamedEnvironmentVariable
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import io.github.diskria.projektor.settings.licenses.License
import io.github.diskria.projektor.settings.projekt.*
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import io.github.diskria.projektor.settings.projekt.common.Projekt
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.internal.extensions.core.extra
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : SettingsExtension() {

    val description: Property<String> = objects.property(String::class.java)
    val tags: SetProperty<String> = objects.setProperty(String::class.java)
    val version: Property<String> = objects.property(String::class.java)
    val license: Property<License> = objects.property(License::class.java)

    val versionCatalogPath: Property<String> = objects.property(String::class.java)

    private var projekt: IProjekt? = null

    fun gradlePlugin(block: GradlePlugin.() -> Unit = {}): GradlePlugin =
        buildProjekt().toGradlePlugin(settings).apply {
            block()
            configure(settings, versionCatalogPath.orNull)
        }

    fun kotlinLibrary(block: KotlinLibrary.() -> Unit = {}): KotlinLibrary =
        buildProjekt().toKotlinLibrary(settings).apply {
            block()
            configure(settings, versionCatalogPath.orNull)
        }

    fun androidLibrary(block: AndroidLibrary.() -> Unit = {}): AndroidLibrary =
        buildProjekt().toAndroidLibrary(settings).apply {
            block()
            configure(settings, versionCatalogPath.orNull)
        }

    fun androidApplication(block: AndroidApplication.() -> Unit = {}): AndroidApplication =
        buildProjekt().toAndroidApplication(settings).apply {
            block()
            configure(settings, versionCatalogPath.orNull)
        }

    fun minecraftMod(block: MinecraftMod.() -> Unit = {}): MinecraftMod =
        buildProjekt().toMinecraftMod(settings).apply {
            block()
            configure(settings, versionCatalogPath.orNull)
        }

    private fun buildProjekt(): Projekt {
        if (projekt != null) {
            gradleError("Projekt already configured!")
        }
        val (owner, repo) = if (settings.providers.isCI) {
            val githubOwner by AutoNamedEnvironmentVariable(isRequired = true)
            val githubRepo by AutoNamedEnvironmentVariable(isRequired = true)
            githubOwner to githubRepo
        } else {
            settings.rootDir.parentFile.asDirectory().name to settings.rootDir.name
        }
        return Projekt(
            owner = owner,
            developer = owner.substringBefore(Constants.Char.HYPHEN),
            repo = repo,
            name = repo.setCase(`kebab-case`, `Title Case`),
            description = requireProperty(description, ::description.name),
            version = requireProperty(version, ::version.name).toSemver().toString(),
            license = requireProperty(license, ::license.name),
            tags = requireProperty(tags, ::tags.name),
        ).also {
            projekt = it
            putExtrasToRootProject(it)
        }
    }

    private fun putExtrasToRootProject(projekt: Projekt) {
        settings.gradle.rootProject {
            description = projekt.description
            version = projekt.version

            val projektOwner by projekt.owner.toAutoNamedProperty()
            val projektDeveloper by projekt.developer.toAutoNamedProperty()
            val projektRepo by projekt.repo.toAutoNamedProperty()
            val projektName by projekt.name.toAutoNamedProperty()
            val projektTags by projekt.tags.toAutoNamedProperty()
            val projektLicenseId by projekt.license.id.toAutoNamedProperty()
            listOf(projektOwner, projektDeveloper, projektRepo, projektName, projektTags, projektLicenseId).forEach {
                extra[it.name] = it.value
            }
        }
    }
}
