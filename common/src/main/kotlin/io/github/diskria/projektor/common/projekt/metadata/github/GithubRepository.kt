package io.github.diskria.projektor.common.projekt.metadata.github

import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.appendPrefix
import io.github.diskria.kotlin.utils.extensions.appendSuffix
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.generics.joinToString
import io.github.diskria.kotlin.utils.extensions.removePrefix
import io.github.diskria.projektor.common.projekt.metadata.github.host.GitHub
import io.ktor.http.*

data class GithubRepository(val owner: GithubOwner, val name: String) {

    val namespace: String
        get() = "io.github".appendPackageName(owner.developerName)

    fun getUrl(isVcs: Boolean = false): String =
        buildUrl(isVcs).toString()

    fun getPath(isVcs: Boolean = false): String =
        buildUrl(isVcs = isVcs).encodedPath.removePrefix(Constants.Char.SLASH)

    fun getIssuesUrl(): String =
        buildUrl {
            path("issues")
        }.toString()

    fun getPackagesMavenUrl(): String =
        buildUrl(isPackagesMaven = true).toString()

    fun buildScmUri(vararg parts: String): String =
        listOf("scm", GitHub.versionControlSystem.name, *parts).joinToString(Constants.Char.COLON)

    fun getSshAuthority(): String =
        GitHub.versionControlSystem.name + Constants.Char.AT_SIGN + GitHub.hostname

    private fun buildUrl(
        isVcs: Boolean = false,
        isPackagesMaven: Boolean = false,
        block: URLBuilder.() -> Unit = {}
    ): Url =
        URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = GitHub.hostname.modifyIf(isPackagesMaven) { it.appendPrefix(PACKAGES_MAVEN_PREFIX) }
            path(
                owner.name,
                name.modifyIf(isVcs) { it.appendSuffix(Constants.Char.DOT + GitHub.versionControlSystem.name) }
            )
            block()
        }.build()

    companion object {
        private const val PACKAGES_MAVEN_PREFIX = "maven.pkg."
    }
}
