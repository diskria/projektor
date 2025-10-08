package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.ProjectExtension
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.projektor.configurations.*
import io.github.diskria.projektor.configurators.*
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.PublishingTarget
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.provideDelegate
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : ProjectExtension() {

    val publishingTarget: Property<PublishingTarget> = objects.property(PublishingTarget::class.java)

    private var configurator: Configurator<*>? = null
    private var onConfiguratorReadyCallback: ((Configurator<*>) -> Unit)? = null

    fun onConfiguratorReady(callback: (Configurator<*>) -> Unit) {
        onConfiguratorReadyCallback = callback
    }

    fun gradlePlugin(block: GradlePluginConfiguration.() -> Unit = {}) {
        setConfigurator(GradlePluginConfigurator(GradlePluginConfiguration().apply(block)))
    }

    fun kotlinLibrary(block: KotlinLibraryConfiguration.() -> Unit = {}) {
        setConfigurator(KotlinLibraryConfigurator(KotlinLibraryConfiguration().apply(block)))
    }

    fun androidLibrary(block: AndroidLibraryConfiguration.() -> Unit = {}) {
        setConfigurator(AndroidLibraryConfigurator(AndroidLibraryConfiguration().apply(block)))
    }

    fun androidApplication(block: AndroidApplicationConfiguration.() -> Unit = {}) {
        setConfigurator(AndroidApplicationConfigurator(AndroidApplicationConfiguration().apply(block)))
    }

    fun minecraftMod(block: MinecraftModConfiguration.() -> Unit = {}) {
        setConfigurator(MinecraftModConfigurator(MinecraftModConfiguration().apply(block)))
    }

    fun buildProjekt(rootProject: Project): Projekt = with(rootProject) {
        val extras = rootProject.extra.properties
        val projektOwner: String by extras
        val projektDeveloper: String by extras
        val projektRepo: String by extras
        val projektName: String by extras
        val projektTags: Set<String> by extras
        val projektLicenseId: String by extras

        val projektDescription = rootProject.description.toNullIfEmpty() ?: gradleError("Description not set!")
        val projektVersion = rootProject.version.toString().toNullIfEmpty() ?: gradleError("Version not set!")

        Projekt(
            owner = projektOwner,
            developer = projektDeveloper,
            email = "diskria@proton.me",
            repo = projektRepo,
            name = projektName,
            description = projektDescription,
            version = projektVersion,
            tags = projektTags,
            license = License.of(projektLicenseId),
            publishingTarget = publishingTarget.orNull,
            javaVersion = Versions.JAVA,
            kotlinVersion = Versions.KOTLIN,
        )
    }

    fun onProjectEvaluated() {
        if (configurator == null) {
            gradleError("Projekt not configured!")
        }
        configurator = null
        onConfiguratorReadyCallback = null
    }

    private fun setConfigurator(configurator: Configurator<*>) {
        if (this.configurator != null) {
            gradleError("Projekt already configured!")
        }
        this.configurator = configurator
        onConfiguratorReadyCallback?.invoke(configurator)
    }
}
