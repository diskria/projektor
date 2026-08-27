package io.github.diskria.projektor.features.generation.readme

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.DistributionTargetType.*
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.license.License
import io.github.diskria.projektor.features.distribution.target.mapToModel
import io.ktor.http.*

internal abstract class ReadmeShield {

    abstract val label: String
    abstract val targetUrl: String?
    abstract val imageUrl: String
    open val alt: String get() = label

    val markdown: String? by lazy {
        targetUrl?.let { MarkdownHelper.link(it, MarkdownHelper.image(imageUrl, alt)) }
    }

    protected fun buildShieldUrl(path: String, vararg queryParameters: Pair<String, String>): String =
        URLBuilder().apply {
            takeFrom("https://img.shields.io/")
            encodedPath = path
            queryParameters.forEach { (key, value) -> parameters.append(key, value) }
        }.buildString()
}

internal abstract class DistributionTargetShield(
    target: DistributionTargetType,
    protected val projekt: Projekt,
) : ReadmeShield() {
    override val label: String = target.displayName
    override val targetUrl: String? = target.mapToModel().getHomepage(projekt)
}

internal abstract class GithubLatestTagShield(
    target: DistributionTargetType,
    projekt: Projekt,
) : DistributionTargetShield(target, projekt) {
    override val imageUrl: String
        get() = buildShieldUrl(
            "github/v/tag/${projekt.metadata.repo.owner.name}/${projekt.name}.svg",
            "label" to label,
            "style" to "for-the-badge",
            "sort" to "semver",
        )
}

internal class GithubPackagesShield(projekt: Projekt) : GithubLatestTagShield(GITHUB_PACKAGES, projekt)

internal class GithubPagesShield(projekt: Projekt) : GithubLatestTagShield(GITHUB_PAGES, projekt)

internal class GradlePluginPortalShield(projekt: Projekt) : DistributionTargetShield(GRADLE_PLUGIN_PORTAL, projekt) {
    override val imageUrl: String
        get() = buildShieldUrl(
            "gradle-plugin-portal/v/${projekt.packageName}.svg",
            "label" to label,
            "style" to "for-the-badge",
        )
}

internal class MavenCentralShield(projekt: Projekt) : DistributionTargetShield(MAVEN_CENTRAL, projekt) {
    override val imageUrl: String
        get() = buildShieldUrl(
            "maven-central/v/${projekt.metadata.repo.owner.namespace}/${projekt.name}.svg",
            "label" to label,
            "style" to "for-the-badge",
        )
}

internal open class StaticShield(
    override val label: String,
    val message: String,
    val color: String,
    override val targetUrl: String? = null,
) : ReadmeShield() {
    override val alt: String get() = "$label: $message"
    override val imageUrl: String
        get() = buildShieldUrl(
            "static/v1",
            "label" to label,
            "message" to message,
            "color" to color,
            "style" to "for-the-badge",
        )
}

internal class LicenseShield(license: License) : StaticShield("License", license.id, "yellow", license.url)
