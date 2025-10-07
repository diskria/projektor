package io.github.diskria.projektor.projekt

import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

open class GradlePlugin(
    projekt: IProjekt,
    project: Project
) : AbstractProjekt(projekt, project), IProjekt by projekt {

    val id: String
        get() = getPackageName()

    var isSettingsPlugin: Boolean = false

    override fun getPackageName(): String =
        projekt.getPackageName().modifyIf(isSettingsPlugin) { it.appendPackageName("settings") }

    override fun configureProject() = with(project) {
        val plugin = this@GradlePlugin
        runExtension<GradlePluginDevelopmentExtension> {
            website.set(getRepoUrl())
            vcsUrl.set(getRepoPath(isVcs = true))
            plugins {
                create(id) {
                    id = plugin.id
                    implementationClass = getPackageName().appendPackageName(getClassNameBase() + "GradlePlugin")
                    displayName = plugin.name
                    description = plugin.description
                    tags.set(plugin.tags.toNullIfEmpty())
                }
            }
        }
    }

    override fun getMetadata(): List<Property<String>> {
        val pluginId by id.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        val pluginName by name.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(pluginId, pluginName)
    }
}
