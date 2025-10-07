package io.github.diskria.projektor.settings.projekt.common

import io.github.diskria.gradle.utils.extensions.files
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPrefix
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.projektor.settings.RepositoriesFilterType
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.initialization.Settings

abstract class AbstractProjekt(val projekt: IProjekt) : IProjekt {

    open fun configureRepositories() {

    }

    open fun configureProjects() {

    }

    fun configure(settings: Settings, versionCatalogPath: String?) = with(settings) {
        applyCommonConfiguration(settings)
        configureRepositories()
        configureProjects()
        versionCatalogPath?.let { path ->
            dependencyResolutionManagement {
                versionCatalogs {
                    create("libs") {
                        from(files(rootDir.resolve(path)))
                    }
                }
            }
        }
    }

    private fun applyCommonConfiguration(settings: Settings) = with(settings) {
        rootProject.name = name.modifyIf(owner.first().isUpperCase()) {
            it.appendPrefix(owner + Constants.Char.SPACE)
        }
        configureRepositories {
            configureMaven(
                "MavenCentralMirror",
                "https://repo1.maven.org/maven2"
            )
            mavenCentral()
        }
        configureRepositories(RepositoriesFilterType.PLUGINS) {
            gradlePluginPortal()
        }
        val licenseTag = "SPDX ID: ${license.id}"
        val licenseFile = rootDir.resolve("LICENSE")
        if (!licenseFile.exists() || licenseFile.readLines().lastOrNull { it.isNotBlank() }?.trim() != licenseTag) {
            licenseFile.writeText(buildString {
                append(runBlocking { getLicenseText() })
                appendLine()
                append(licenseTag)
                appendLine()
            })
        }
    }

    private suspend fun getLicenseText(): String =
        HttpClient(CIO).use { client ->
            val template = client.get(license.url).bodyAsText()
            license.fillTemplate(template, this)
        }
}
