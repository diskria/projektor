package io.github.diskria.projektor.common.projekt.metadata.github

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPrefix
import io.github.diskria.kotlin.utils.extensions.appendSuffix
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.generics.joinToString
import io.github.diskria.kotlin.utils.extensions.removePrefix
import io.github.diskria.projektor.common.repository.RepositoryHost
import io.github.diskria.projektor.common.repository.VersionControlSystem
import io.ktor.http.*

data class GithubRepository(val owner: GithubOwner, val name: String) {

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
        buildUrl("${owner.developerName}.${RepositoryHost.GITHUB.shortName}.io", URLProtocol.HTTPS) {
            path(name)
        }

    fun buildScmUri(vararg parts: String): String =
        listOf("scm", VersionControlSystem.GIT.shortName, *parts).joinToString(Constants.Char.COLON)

    fun getSshAuthority(): String =
        VersionControlSystem.GIT.shortName + Constants.Char.AT_SIGN + RepositoryHost.GITHUB.hostname

    private fun buildGithubUrl(
        isVcs: Boolean = false,
        isPackagesMaven: Boolean = false,
        block: URLBuilder.() -> Unit = {}
    ): Url =
        URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = RepositoryHost.GITHUB.hostname.modifyIf(isPackagesMaven) { it.appendPrefix(PACKAGES_MAVEN_PREFIX) }
            path(
                owner.name,
                name.modifyIf(isVcs) { it.appendSuffix(Constants.Char.DOT + VersionControlSystem.GIT.shortName) }
            )
            block()
        }.build()

    companion object {
        private const val PACKAGES_MAVEN_PREFIX = "maven.pkg."
    }
}
