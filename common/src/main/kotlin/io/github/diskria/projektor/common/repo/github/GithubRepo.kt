package io.github.diskria.projektor.common.repo.github

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.generics.joinToString
import io.github.diskria.kotlin.utils.extensions.removePrefix
import io.github.diskria.kotlin.utils.extensions.reverseSegments
import io.github.diskria.projektor.common.repo.RepoHost
import io.github.diskria.projektor.common.repo.VCS
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class GithubRepo(val owner: GithubOwner, val name: String) {

    fun getUrl(isVcs: Boolean = false, token: String? = null): String =
        buildRepoUrl(isVcs = isVcs, token = token).toString()

    fun getPath(isVcs: Boolean = false): String =
        buildRepoUrl(isVcs = isVcs).encodedPath.removePrefix(Constants.Char.SLASH)

    fun getIssuesUrl(): String =
        buildRepoUrl {
            appendPathSegments("issues")
        }.toString()

    fun getPackagesMavenUrl(): String =
        buildRepoUrl(isPackagesMaven = true).toString()

    fun getHostName(): String =
        owner.namespace.reverseSegments(Constants.Char.DOT)

    fun getPagesUrl(): Url =
        buildUrl(getHostName()) {
            path(name)
        }

    fun getScmConnectionUrl(): String =
        buildScmUri(getUrl(isVcs = true))

    fun getScmDeveloperConnectionUrl(): String =
        buildScmUri(getSshAuthority(), getPath(isVcs = true))

    private fun buildScmUri(vararg parts: String): String =
        listOf("scm", GIT.shortName, *parts).joinToString(Constants.Char.COLON)

    private fun getSshAuthority(): String =
        GIT.shortName + Constants.Char.AT_SIGN + RepoHost.GITHUB.hostName

    private fun buildRepoUrl(
        isVcs: Boolean = false,
        isPackagesMaven: Boolean = false,
        token: String? = null,
        build: URLBuilder.() -> Unit = {}
    ): Url =
        URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            token?.let {
                user = BASIC_AUTH_USERNAME
                password = token
            }
            host = RepoHost.GITHUB.hostName.modifyIf(isPackagesMaven) { PACKAGES_MAVEN_PREFIX + it }
            path(owner.name, name.modifyIf(isVcs) { it + Constants.Char.DOT + GIT.shortName })
            build(this)
        }.build()

    companion object {
        private const val BASIC_AUTH_USERNAME: String = "x-access-token"
        private const val PACKAGES_MAVEN_PREFIX: String = "maven.pkg."
        private val GIT: VCS = RepoHost.GITHUB.vcs
    }
}
