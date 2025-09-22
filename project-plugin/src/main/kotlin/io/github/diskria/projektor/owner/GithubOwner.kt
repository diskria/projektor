package io.github.diskria.projektor.owner

import io.github.diskria.projektor.projekt.SoftwareForgeType
import io.github.diskria.projektor.projekt.logicalName
import io.github.diskria.utils.kotlin.Constants
import io.github.diskria.utils.kotlin.extensions.appendPrefix
import io.github.diskria.utils.kotlin.extensions.appendSuffix
import io.github.diskria.utils.kotlin.extensions.common.modifyIf
import io.github.diskria.utils.kotlin.extensions.mappers.toName
import io.github.diskria.utils.kotlin.extensions.removePrefix
import io.ktor.http.*

abstract class GithubOwner(name: String) : ProjektOwner(name, SoftwareForgeType.GITHUB) {

    abstract override val email: String

    override val namespace: String =
        "io.${softwareForgeType.toName()}.${name.lowercase()}"

    fun getPackagesMavenUrl(slug: String): String =
        buildRepositoryUrl(slug, isMaven = true).toString()

    override fun getRepositoryUrl(slug: String, isVcsUrl: Boolean): String =
        buildRepositoryUrl(slug, isVcsUrl).toString()

    override fun getRepositoryPath(slug: String, isVcsUrl: Boolean): String =
        buildRepositoryUrl(slug, isVcs = isVcsUrl).encodedPath.removePrefix(Constants.Char.SLASH)

    override fun getIssuesUrl(slug: String): String =
        buildRepositoryUrl(slug) {
            path("issues")
        }.toString()

    private fun buildRepositoryUrl(
        slug: String,
        isVcs: Boolean = false,
        isMaven: Boolean = false,
        extraBuild: URLBuilder.() -> Unit = {},
    ): Url =
        URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = softwareForgeType.hostname.modifyIf(isMaven) { it.appendPrefix("maven.pkg.") }
            path(name, slug.modifyIf(isVcs) { it.appendSuffix(".${softwareForgeType.scmType.logicalName()}") })
            extraBuild()
        }.build()
}
