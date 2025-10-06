package io.github.diskria.projektor.projekt.common

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.Semver
import io.github.diskria.kotlin.utils.extensions.*
import io.github.diskria.kotlin.utils.extensions.common.*
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.repo.host.GitHub
import io.github.diskria.projektor.repo.host.RepoHost
import io.ktor.http.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

interface IProjekt {

    val owner: String
    val repo: String
    val description: String
    val semver: Semver
    val email: String
    val license: License
    val javaVersion: Int
    val jvmTarget: JvmTarget
    val kotlinVersion: String

    val repoHost: RepoHost
        get() = GitHub

    val name: String
        get() = repo.setCase(`kebab-case`, `Title Case`)

    val developer: String
        get() = if (owner.contains("-")) owner.substringBefore("-") else owner

    val namespace: String
        get() = "io.github".appendPackageName(developer)

    val packageName: String
        get() = namespace.appendPackageName(repo.setCase(`kebab-case`, `dot․case`))

    val packagePath: String
        get() = packageName.setCase(`dot․case`, `path∕case`)

    val classNameBase: String
        get() = repo.setCase(`kebab-case`, PascalCase)

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
            host = repoHost.hostname.modifyIf(isPackages) { it.appendPrefix("maven.pkg.") }
            path(owner, repo.modifyIf(isVcs) { it.appendSuffix(".${repoHost.versionControlSystem.name}") })
            block()
        }.build()
}
