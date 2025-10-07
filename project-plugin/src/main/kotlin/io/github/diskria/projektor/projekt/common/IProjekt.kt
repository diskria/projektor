package io.github.diskria.projektor.projekt.common

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.*
import io.github.diskria.kotlin.utils.extensions.common.`dot․case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.common.`path∕case`
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.publishing.PublishingTarget
import io.github.diskria.projektor.repo.host.GitHub
import io.github.diskria.projektor.repo.host.RepoHost
import io.ktor.http.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

interface IProjekt {

    val owner: String
    val developer: String
    val email: String
    val repo: String
    val name: String
    val description: String
    val tags: Set<String>
    val version: String
    val license: License
    val publishingTarget: PublishingTarget?
    val javaVersion: Int
    val kotlinVersion: String

    fun getNamespace(): String =
        "io.github".appendPackageName(developer)

    fun getPackageName(): String =
        getNamespace().appendPackageName(repo.setCase(`kebab-case`, `dot․case`))

    fun getPackagePath(): String =
        getPackageName().setCase(`dot․case`, `path∕case`)

    fun getClassNameBase(): String =
        repo.setCase(`kebab-case`, PascalCase)

    fun getJvmTarget(): JvmTarget =
        javaVersion.toJvmTarget()

    fun getJarVersion(): String =
        version

    fun getRepoHost(): RepoHost =
        GitHub

    val githubPackagesUrl: String
        get() = buildGithubUrl(isPackages = true).toString()

    val githubIssuesUrl: String
        get() = buildGithubUrl { path("issues") }.toString()

    fun getRepoUrl(isVcs: Boolean = false): String =
        buildGithubUrl(isVcs).toString()

    fun getRepoPath(isVcs: Boolean = false): String =
        buildGithubUrl(isVcs = isVcs).encodedPath.removePrefix(Constants.Char.SLASH)

    fun getMetadata(): List<Property<String>> = emptyList()

    private fun buildGithubUrl(
        isVcs: Boolean = false,
        isPackages: Boolean = false,
        block: URLBuilder.() -> Unit = {}
    ): Url =
        URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = getRepoHost().hostname.modifyIf(isPackages) { it.appendPrefix("maven.pkg.") }
            path(owner, repo.modifyIf(isVcs) { it.appendSuffix(".${getRepoHost().versionControlSystem.name}") })
            block()
        }.build()
}
