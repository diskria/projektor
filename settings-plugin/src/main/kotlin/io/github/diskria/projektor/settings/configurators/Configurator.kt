package io.github.diskria.projektor.settings.configurators

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPrefix
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import io.github.diskria.projektor.settings.repositories.PluginRepositories
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.initialization.Settings

sealed class Configurator<T : IProjekt> {

    abstract fun configure(settings: Settings, projekt: IProjekt): T

    protected fun applyCommonConfiguration(settings: Settings, projekt: T) = with(settings) {
        rootProject.name = projekt.name.modifyIf(projekt.owner.first().isUpperCase()) {
            it.appendPrefix(projekt.owner + Constants.Char.SPACE)
        }
        configureRepositories {
            configureMaven(
                "MavenCentralMirror",
                "https://repo1.maven.org/maven2"
            )
            mavenCentral()
        }
        configureRepositories(PluginRepositories) {
            gradlePluginPortal()
        }
        generateLicense(settings, projekt)
    }

    private fun generateLicense(settings: Settings, projekt: IProjekt) = with(settings) {
        val licenseTag = "SPDX ID: ${projekt.license.id}"
        val licenseFile = rootDir.resolve("LICENSE")
        if (licenseFile.exists() && licenseFile.readLines().lastOrNull { it.isNotBlank() }?.trim() == licenseTag) {
            return
        }
        licenseFile.writeText(buildString {
            append(runBlocking { getLicenseText(projekt) })
            appendLine()
            append(licenseTag)
            appendLine()
        })
    }

    private suspend fun getLicenseText(projekt: IProjekt): String =
        HttpClient(CIO).use { client ->
            val template = client.get(projekt.license.url).bodyAsText()
            projekt.license.fillTemplate(template, projekt)
        }
}
