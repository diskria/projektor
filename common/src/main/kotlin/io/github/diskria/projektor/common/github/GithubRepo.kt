package io.github.diskria.projektor.common.github

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.generics.joinToString
import io.github.diskria.kotlin.utils.extensions.removePrefix
import io.github.diskria.projektor.common.projekt.OwnerType
import io.github.diskria.projektor.common.repository.RepositoryHost
import io.github.diskria.projektor.common.repository.VersionControlSystem
import io.ktor.http.*

data class GithubRepo(val owner: String, val name: String) {

    val namespace: String
        get() = "io".appendPackageName(RepositoryHost.GITHUB.shortName).appendPackageName(developerName)

    val ownerType: OwnerType
        get() = when {
            owner.first().isUpperCase() -> OwnerType.BRAND
            owner.contains(Constants.Char.HYPHEN) -> OwnerType.DOMAIN
            else -> OwnerType.PROFILE
        }

    val developerName: String
        get() = name.lowercase().modifyIf(ownerType == OwnerType.DOMAIN) { it.substringBefore(Constants.Char.HYPHEN) }

    fun getUrl(isVcs: Boolean = false): String =
        buildGithubUrl(isVcs).toString()

    fun getPath(isVcs: Boolean = false): String =
        buildGithubUrl(isVcs = isVcs).encodedPath.removePrefix(Constants.Char.SLASH)

    fun getIssuesUrl(): String =
        buildGithubUrl {
            path("issues")
        }.toString()

    fun getPackagesMavenUrl(): String =
        buildGithubUrl(isPackagesMaven = true).toString()

    fun getPagesUrl(): String =
        getPagesUrl(developerName, name)

    fun buildScmUri(vararg parts: String): String =
        listOf("scm", VERSION_CONTROL_SYSTEM.shortName, *parts).joinToString(Constants.Char.COLON)

    fun getSshAuthority(): String =
        VERSION_CONTROL_SYSTEM.shortName + Constants.Char.AT_SIGN + RepositoryHost.GITHUB.hostName

    private fun buildGithubUrl(
        isVcs: Boolean = false,
        isPackagesMaven: Boolean = false,
        block: URLBuilder.() -> Unit = {}
    ): Url =
        URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = RepositoryHost.GITHUB.hostName.modifyIf(isPackagesMaven) { PACKAGES_MAVEN_PREFIX + it }
            path(owner, name.modifyIf(isVcs) { it + Constants.Char.DOT + VERSION_CONTROL_SYSTEM.shortName })
            block()
        }.build()

    companion object {

        private const val PACKAGES_MAVEN_PREFIX: String = "maven.pkg."
        private val VERSION_CONTROL_SYSTEM: VersionControlSystem = RepositoryHost.GITHUB.versionControlSystem

        fun getPagesUrl(developerName: String, repositoryName: String): String =
            buildUrl("$developerName.${RepositoryHost.GITHUB.shortName}.io") {
                path(repositoryName)
            }
    }
}
