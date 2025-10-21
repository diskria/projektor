package io.github.diskria.projektor.common.repo.github

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.generics.joinToString
import io.github.diskria.kotlin.utils.extensions.removePrefix
import io.github.diskria.projektor.common.repo.RepoHost
import io.github.diskria.projektor.common.repo.VCS
import io.ktor.http.*

data class GithubRepo(val owner: GithubOwner, val name: String) {

    fun getUrl(isVcs: Boolean = false, token: String? = null): String =
        buildRepoUrl(isVcs = isVcs, token = token).toString()

    fun getPath(isVcs: Boolean = false): String =
        buildRepoUrl(isVcs = isVcs).encodedPath.removePrefix(Constants.Char.SLASH)

    fun getIssuesUrl(): String =
        buildRepoUrl {
            path("issues")
        }.toString()

    fun getPackagesMavenUrl(): String =
        buildRepoUrl(isPackagesMaven = true).toString()

    fun getHostName(): String =
        owner.namespace.split(Constants.Char.DOT).reversed().joinToString(Constants.Char.DOT)

    fun getPagesUrl(): String =
        buildUrl(getHostName()) {
            path(name)
        }

    fun buildScmUri(vararg parts: String): String =
        listOf("scm", VERSION_CONTROL_SYSTEM.shortName, *parts).joinToString(Constants.Char.COLON)

    fun getSshAuthority(): String =
        VERSION_CONTROL_SYSTEM.shortName + Constants.Char.AT_SIGN + RepoHost.GITHUB.hostName

    private fun buildRepoUrl(
        isVcs: Boolean = false,
        isPackagesMaven: Boolean = false,
        token: String? = null,
        block: URLBuilder.() -> Unit = {}
    ): Url =
        URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            token?.let {
                user = BASIC_AUTH_USERNAME
                password = token
            }
            host = RepoHost.GITHUB.hostName.modifyIf(isPackagesMaven) { PACKAGES_MAVEN_PREFIX + it }
            path(owner.name, name.modifyIf(isVcs) { it + Constants.Char.DOT + VERSION_CONTROL_SYSTEM.shortName })
            block()
        }.build()

    companion object {
        private const val BASIC_AUTH_USERNAME: String = "x-access-token"
        private const val PACKAGES_MAVEN_PREFIX: String = "maven.pkg."
        private val VERSION_CONTROL_SYSTEM: VCS = RepoHost.GITHUB.vcs

        fun getPagesUrl(owner: String, repo: String): String =
            GithubRepo(GithubOwner(owner, Constants.Char.EMPTY), repo).getPagesUrl()
    }
}
