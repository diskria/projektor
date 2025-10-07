package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.gradle.ProjectExtension
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.projekt.*
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.PublishingTarget
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.provideDelegate
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : ProjectExtension() {

    val publishingTarget: Property<PublishingTarget> = objects.property(PublishingTarget::class.java)

    private var projekt: IProjekt? = null

    fun gradlePlugin(block: GradlePlugin.() -> Unit = {}): GradlePlugin =
        toProjekt().toGradlePlugin(project).apply {
            block()
            configureProject()
        }

    fun kotlinLibrary(block: KotlinLibrary.() -> Unit = {}): KotlinLibrary =
        toProjekt().toKotlinLibrary(project).apply {
            block()
            configureProject()
        }

    fun androidLibrary(block: AndroidLibrary.() -> Unit = {}): AndroidLibrary =
        toProjekt().toAndroidLibrary(project).apply {
            block()
            configureProject()
        }

    fun androidApplication(block: AndroidApplication.() -> Unit = {}): AndroidApplication =
        toProjekt().toAndroidApplication(project).apply {
            block()
            configureProject()
        }

    fun minecraftMod(block: MinecraftMod.() -> Unit = {}): MinecraftMod =
        toProjekt().toMinecraftMod(project).apply {
            block()
            configureProject()
        }

    private fun toProjekt(): Projekt {
        if (projekt != null) {
            gradleError("Projekt already configured!")
        }
        val rootProject = project.rootProject
        val extras = rootProject.extra.properties
        val projektOwner: String by extras
        val projektDeveloper: String by extras
        val projektRepo: String by extras
        val projektName: String by extras
        val projektDescription = rootProject.description.toNullIfEmpty() ?: gradleError("Description not set!")
        val projektVersion = rootProject.version.toString().toNullIfEmpty() ?: gradleError("Version not set!")
        val projektTags: Set<String> by extras
        val projektLicenseId: String by extras
        return Projekt(
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
        ).also {
            projekt = it
        }
    }
}
