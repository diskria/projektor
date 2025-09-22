package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.licenses.MitLicense
import io.github.diskria.projektor.owner.GithubOwner
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.Projekt
import io.github.diskria.projektor.projekt.PublishingTarget
import io.github.diskria.utils.kotlin.Constants
import org.gradle.api.Project
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

fun <R> Project.gradlePlugin(block: GradlePluginDevelopmentExtension.() -> R): R =
    getExtensionOrThrow<GradlePluginDevelopmentExtension>().block()

fun Project.configureGradlePlugin(
    owner: GithubOwner,
    publishingTarget: PublishingTarget? = null,
    isSettingsPlugin: Boolean = false,
    tags: Set<String> = emptySet(),
    license: License = MitLicense,
): GradlePlugin {
    requirePlugins("maven-publish")
    val plugin = Projekt.of(this, owner, license).toGradlePlugin(isSettingsPlugin)
    gradlePlugin {
        website.set(owner.getRepositoryUrl(plugin.slug))
        vcsUrl.set(owner.getRepositoryUrl(plugin.slug, isVcsUrl = true))

        plugins {
            create(plugin.id) {
                id = plugin.id
                implementationClass = plugin.packageName + Constants.Char.DOT + plugin.className

                displayName = plugin.name
                description = plugin.description

                if (tags.isNotEmpty()) {
                    this.tags.set(tags)
                }
            }
        }
    }
    configureProjekt(plugin)
    configurePublishing(plugin, publishingTarget)
    return plugin
}
