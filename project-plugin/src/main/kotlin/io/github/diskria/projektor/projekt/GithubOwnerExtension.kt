package io.github.diskria.projektor.projekt

import io.github.diskria.gradle.utils.extensions.gradle.ProjectExtension
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPrefix
import io.github.diskria.kotlin.utils.extensions.appendSuffix
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.removePrefix
import io.ktor.http.*
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class GithubOwnerExtension @Inject constructor(objects: ObjectFactory) : ProjectExtension() {

    val name: Property<String> = objects.property(String::class.java)
    val repo: Property<String> = objects.property(String::class.java)
    val email: Property<String> = objects.property(String::class.java)

    val namespace: Property<String> = objects.property(String::class.java).apply {
        set(name.map { "io.github.${it.lowercase()}" })
    }

    fun getPackagesMavenUrl(): String =
        buildRepositoryUrl(isMaven = true).toString()

    fun getRepositoryUrl(isVcsUrl: Boolean): String =
        buildRepositoryUrl(isVcsUrl).toString()

    fun getRepositoryPath(isVcsUrl: Boolean): String =
        buildRepositoryUrl(isVcs = isVcsUrl)
            .encodedPath
            .removePrefix(Constants.Char.SLASH)

    fun getIssuesUrl(): String =
        buildRepositoryUrl {
            path("issues")
        }.toString()

    private fun buildRepositoryUrl(
        isVcs: Boolean = false,
        isMaven: Boolean = false,
        extraBlock: URLBuilder.() -> Unit = {},
    ): Url =
        URLBuilder().apply {
            val name = requireProperty(name, ::name.name)
            val repo = requireProperty(repo, ::repo.name)
            protocol = URLProtocol.HTTPS
            host = "github.com".modifyIf(isMaven) { it.appendPrefix("maven.pkg.") }
            path(name, repo.modifyIf(isVcs) { it.appendSuffix(".git") })
            extraBlock()
        }.build()
}
