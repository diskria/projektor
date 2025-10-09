package io.github.diskria.projektor.settings.configurators

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPrefix
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.pluginRepositories
import io.github.diskria.projektor.settings.extensions.repositories
import io.github.diskria.projektor.settings.projekt.ProjektMetadata
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.initialization.Settings

sealed class Configurator {

    open fun configure(settings: Settings, metadata: ProjektMetadata) {
        configureRootProject(settings, metadata)
        configureRepositories(settings)
        configureProjects(settings)
        configureLicense(settings, metadata)
    }

    protected open fun configureRepositories(settings: Settings) = with(settings) {
        repositories {
            configureMaven(
                "MavenCentralMirror",
                "https://repo1.maven.org/maven2"
            )
            mavenCentral()
        }
        pluginRepositories {
            gradlePluginPortal()
        }
    }

    protected open fun configureProjects(settings: Settings) {

    }

    private fun configureRootProject(settings: Settings, metadata: ProjektMetadata) = with(settings) {
        rootProject.name = metadata.name.modifyIf(metadata.owner.first().isUpperCase()) {
            it.appendPrefix(metadata.owner + Constants.Char.SPACE)
        }
    }

    private fun configureLicense(settings: Settings, metadata: ProjektMetadata) = with(settings) {
        val licenseTag = "SPDX ID: ${metadata.license.id}"
        val licenseFile = rootDir.resolve("LICENSE")
        if (licenseFile.exists() && licenseFile.readLines().lastOrNull { it.isNotBlank() }?.trim() == licenseTag) {
            return
        }
        licenseFile.writeText(buildString {
            append(runBlocking { getLicenseText(metadata) })
            appendLine()
            append(licenseTag)
            appendLine()
        })
    }

    private suspend fun getLicenseText(metadata: ProjektMetadata): String =
        HttpClient(CIO).use { client ->
            val template = client.get(metadata.license.url).bodyAsText()
            metadata.license.fillTemplate(template, metadata)
        }
}
